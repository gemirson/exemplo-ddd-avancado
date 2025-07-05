package org.com.pangolin.dominio.parcela;

import org.com.pangolin.dominio.amortizacao.MemorialDeAmortizacao;
import org.com.pangolin.dominio.core.Entidade;
import org.com.pangolin.dominio.core.Resultado;
import org.com.pangolin.dominio.core.validacoes.Validador;
import org.com.pangolin.dominio.enums.StatusParcelaEnum;
import org.com.pangolin.dominio.excecoes.RegraDeNegocioException;
import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;
import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiroValidadorFactory;
import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.estados.EstadoAberta;
import org.com.pangolin.dominio.parcela.estados.EstadoVencida;
import org.com.pangolin.dominio.parcela.estados.IEstadoParcela;
import org.com.pangolin.dominio.parcela.estrategias.IEstrategiaDeDistribuicaoDeAmortizacao;
import org.com.pangolin.dominio.parcela.estrategias.ResultadoDistribuicao;
import org.com.pangolin.dominio.servicos.IServicoCalculoEncargos;
import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.ErroDeValidacao;
import org.com.pangolin.dominio.vo.Pagamento;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final  class Parcela extends Entidade<Integer, ParcelaId> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ParcelaId parcelaId;
    private final LocalDate dataVencimento = LocalDate.now().plusMonths(1); // Exemplo de vencimento para 1 mês a partir de hoje
    private final ValorMonetario valorParcela;

    // Lista de componentes financeiros que compõem a parcela.

    private final List<ComponenteFinanceiro> componentesFinanceiros;
    // CAMPO DE CACHE: final, para garantir que seja atribuído apenas uma vez.
    private Map<TipoComponente, ComponenteFinanceiro> componentesMap;
    private IEstadoParcela estado;
    private final IServicoCalculoEncargos servicoEncargos;

    // O "BLUEPRINT": Define a estrutura mínima obrigatória para qualquer parcela.
    private static final Set<TipoComponente> COMPONENTES_ESSENCIAIS = Set.of(
            TipoComponente.PRINCIPAL,
            TipoComponente.JUROS
            // Se JUROS também fosse essencial, seria adicionado aqui:
            // , TipoComponente.JUROS
    );

    public Parcela(
            ParcelaId id,
            ValorMonetario valorParcela,
            LocalDate dataDeReferencia,
            List<ComponenteFinanceiro> componentesFinanceirosIniciais,
            IServicoCalculoEncargos servicoEncargos) {
        super( Objects.requireNonNull(id, "O ID da parcela não pode ser nulo."));

        // =================================================================
        // BLOCO DE VALIDAÇÃO (O GUARDIÃO)
        // =================================================================

        Objects.requireNonNull(dataVencimento, "A data de vencimento não pode ser nula.");
        Objects.requireNonNull(valorParcela, " O Valor da parcela não pode ser nula.");
        Objects.requireNonNull(componentesFinanceirosIniciais, "A lista de componentes iniciais não pode ser nula.");
        Objects.requireNonNull(servicoEncargos, "O serviço de cálculo de encargos não pode ser nulo.");
        //Objects.requireNonNull(contrato, "A referência ao contrato (Carteira) não pode ser nula.");

        // Valida se a parcela tem todos os componentes essenciais.
        validarComponentesEssenciais(componentesFinanceirosIniciais);

        if (dataVencimento.isBefore(dataDeReferencia)) {
            throw new IllegalArgumentException("A data de vencimento não pode ser no passado.");
        }
        if (componentesFinanceirosIniciais.isEmpty()) {
            throw new IllegalArgumentException("A parcela deve ser criada com pelo menos um componente financeiro.");
        }

        // Valida se todos os componentes financeiros são válidos.
        // Obtém o validador da nossa fábrica.
        Validador<ComponenteFinanceiro> validadorComponente = ComponenteFinanceiroValidadorFactory.validador();

        // Itera sobre os componentes recebidos e valida cada um deles.
        for (ComponenteFinanceiro componente : componentesFinanceirosIniciais) {
            Resultado<ComponenteFinanceiro, List<ErroDeValidacao>> resultado = validadorComponente.validar(componente);

            if (resultado.isErro()) {
                // Se qualquer componente for inválido, a criação da Parcela falha imediatamente.
                String errosConcatenados = resultado.getErro().orElse(List.of()).stream()
                        .map(ErroDeValidacao::mensagem)
                        .collect(Collectors.joining("; "));
                throw new RegraDeNegocioException(
                        "Componente financeiro inválido na criação da parcela. Erros: " + errosConcatenados
                );
            }
        }
        this.parcelaId = id;
        this.valorParcela = valorParcela;
        this.componentesFinanceiros = new ArrayList<>(componentesFinanceirosIniciais);
        this.servicoEncargos = servicoEncargos;

        this.componentesMap = null; // Inicialmente nulo, será construído sob demanda

        this.estado = new EstadoAberta(this.servicoEncargos); // Define o estado inicial
    }

    public ParcelaId parcelaId() {
        return parcelaId;
    }
    public ValorMonetario valorParcela() {
        return valorParcela;
    }


    public ParcelaId Id(){
        return this.parcelaId;
    }

    // O método polimórfico. A "mágica" do OCP acontece aqui.
    // A Carteira chamará este método sem saber como ele é implementado.
    /**
     * Retorna o valor atualizado da parcela.
     * Este método é final porque o comportamento é inteiramente delegado
     * para o objeto de estado, que implementa a lógica polimórfica.
     */
    public final ValorMonetario getValorAtualizado(LocalDate dataDeReferencia) {
        return this.estado.valorAtualizado(this, dataDeReferencia);
    }


    /**
     * Calcula o número de dias de atraso em relação a uma data de referência.
     * Um método puro e facilmente testável.
     * @param dataDeReferencia A data contra a qual o vencimento será comparado (normalmente, "hoje").
     * @return o número de dias de atraso; 0 se não estiver atrasado.
     */
    public long diasDeAtraso(LocalDate dataDeReferencia) {
        if (dataDeReferencia.isAfter(this.dataVencimento)) {
            return ChronoUnit.DAYS.between(this.dataVencimento, dataDeReferencia);
        }
        return 0;
    }

    /**
     * Calcula o número de dias de antecipação em relação a uma data de referência.
     * É o oposto simétrico de getDiasDeAtraso.
     * @param dataDeReferencia A data a partir da qual o cálculo é feito (normalmente, "hoje").
     * @return o número de dias de antecipação; 0 se a data de referência for igual ou posterior ao vencimento.
     */
    public long diasParaAntecipacao(LocalDate dataDeReferencia) {
        if (this.dataVencimento.isAfter(dataDeReferencia)) {
            return ChronoUnit.DAYS.between(dataDeReferencia, this.dataVencimento);
        }
        return 0;
    }
    public ValorMonetario baseDeCalculoParaMulta() {
        // A Parcela sabe como calcular sua própria base para multa.
        // A regra de negócio está encapsulada aqui.
        return this.componentes().stream()
                .filter(c -> c.tipo() == TipoComponente.PRINCIPAL || c.tipo() == TipoComponente.JUROS)
                .map(ComponenteFinanceiro::saldoDevedor)
                .reduce(ValorMonetario.ZERO, ValorMonetario::somar);
    }

    /**
     * Garante que a lista de componentes fornecida na criação contém todos os
     * tipos de componentes definidos como essenciais.
     *
     * @param componentesIniciais A lista de componentes a ser validada.
     * @throws IllegalArgumentException Se algum componente essencial estiver faltando.
     */
    private void validarComponentesEssenciais(List<ComponenteFinanceiro> componentesIniciais) {
        // Cria um Set com os tipos de componentes que foram fornecidos para uma busca eficiente.
        Set<TipoComponente> tiposFornecidos = componentesIniciais.stream()
                .map(ComponenteFinanceiro::tipo)
                .collect(Collectors.toSet());

        // Para cada componente essencial definido em nosso "blueprint"...
        for (TipoComponente essencial : COMPONENTES_ESSENCIAIS) {
            // ...verifica se ele está presente na lista de componentes fornecida.
            if (!tiposFornecidos.contains(essencial)) {
                throw new IllegalArgumentException(
                        "Criação de parcela inválida. O componente essencial '" + essencial + "' está faltando."
                );
            }
        }
    }

    /**
     * Retorna o mapa de componentes cacheado para buscas rápidas.
     * Este método agora é extremamente rápido, pois apenas retorna uma referência.
     */
    private Map<TipoComponente, ComponenteFinanceiro> componentesMap() {
        if (this.componentesMap == null) {
            System.out.println("LOG: Cache de componentesMap está sujo/nulo. Reconstruindo...");
            this.componentesMap = this.componentesFinanceiros.stream()
                    .collect(Collectors.toUnmodifiableMap(ComponenteFinanceiro::tipo, Function.identity()));
        }
        return this.componentesMap;
    }

    public  List<ComponenteFinanceiro> componentes() {
        // Retorna uma cópia imutável da lista de componentes financeiros
        return Collections.unmodifiableList(componentesFinanceiros);
    }

    public MemorialDeAmortizacao pagar(
            Pagamento pagamento,
            IEstrategiaDeDistribuicaoDeAmortizacao estrategia,
            LocalDate dataDeReferencia) {
        // Delega para o objeto de estado atual. AGORA, this.estado é um EstadoAberta.
        return this.estado.pagar(this, pagamento, estrategia,dataDeReferencia);
    }


    public void cancelar(LocalDate dataDeReferencia) {
        // Delega a ação para o objeto de estado atual, passando a si mesma como contexto.
        this.estado.cancelar(this,dataDeReferencia);
    }
    /**
     * Método privado que efetivamente altera o estado dos componentes
     * com base nos resultados calculados pela estratégia.
     */
    public void aplicarResultadoDistribuicao(ResultadoDistribuicao resultado) {
        Map<TipoComponente, ComponenteFinanceiro> mapaComponentes = componentesMap();

        for (DetalheAplicacaoComponente detalhe : resultado.detalhes()) {
            ComponenteFinanceiro componenteParaAtualizar = mapaComponentes.get(detalhe.tipo());
            if (componenteParaAtualizar != null) {
                // A Parcela, como dona de seu estado, confirma a mudança.
                // Aqui, estamos assumindo que ComponenteFinanceiro é mutável internamente
                // para este modelo de estado.
                componenteParaAtualizar.atualizarSaldoDevedor(detalhe.saldoNovo());
            }
        }
    }

    /**
     * A Parcela, e somente ela, executa o plano de mutação em seus próprios componentes.
     */
    public void aplicarPlanoDeDistribuicao(ResultadoDistribuicao plano) {

        // Aqui, usamos o método componentesMap() para obter uma visão mutável do mapa de componentes.
        Map<TipoComponente, ComponenteFinanceiro> mapaMutavel = this.componentesMap();

        System.out.println("LOG: Parcela aplicando plano de distribuição em seu próprio estado...");
        for (DetalheAplicacaoComponente detalhe : plano.detalhes()) {
            ComponenteFinanceiro componenteParaModificar = mapaMutavel.get(detalhe.tipo());
            if (componenteParaModificar != null) {
                // A mutação final é feita aqui, dentro da entidade guardiã.
                componenteParaModificar.atualizarSaldoDevedor(detalhe.saldoNovo());
            }
        }
    }
    /**
     * Usa os mesmos detalhes para construir o registro histórico final.
     */
    public MemorialDeAmortizacao criarMemorial(Pagamento pagamento, ResultadoDistribuicao resultado, String nomeEstrategia) {
        return new MemorialDeAmortizacao(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                pagamento.valor(),
                nomeEstrategia,
                resultado.detalhes(), // A lista de detalhes é usada diretamente aqui
                pagamento.valor().subtrair(resultado.valorNaoUtilizado()),
                resultado.valorNaoUtilizado()
        );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parcela)) return false;
        Parcela parcelas = (Parcela) o;
        return parcelaId.equals(parcelas.parcelaId);
    }

    @Override
    public int hashCode() {
        return parcelaId.hashCode();
    }

    /**
     * Método package-private ou protected para permitir que apenas as classes de estado
     * possam mudar o estado da parcela.
     */
    public void transicionarPara(IEstadoParcela novoEstado) {
        System.out.println("LOG: Transição de estado -> " + novoEstado.getClass().getSimpleName());
        this.estado = novoEstado;
    }

    // Método interno para a lógica de reversão, chamado pelo EstadoPaga.
    public void reverterPagamentoInterno(MemorialDeAmortizacao memorial) {
        // ... lógica que já tínhamos para reverter os saldos dos componentes ...
    }

    /**
     * Método privado que encapsula a lógica de verificação e transição.
     * @return true se o estado foi alterado para Vencida, false caso contrário.
     */
    private boolean verificarETransicionarSeVencida(Parcela parcela,LocalDate dataDeReferencia) {
        if (dataDeReferencia.isAfter(parcela.dataVencimento())) {
            parcela.transicionarPara(new EstadoVencida(this.servicoEncargos));
            return true;
        }
        return false;
    }

    // Getter para o status enum (útil para persistência e DTOs)
    public StatusParcelaEnum status() {
        return this.estado.status();
    }

    /**
     * Método para obter o saldo devedor total da parcela.
     * Soma os saldos de todos os componentes financeiros.
     */
    public ValorMonetario saldoDevedor() {
        return null;
    }

    public LocalDate dataVencimento() {
          return dataVencimento;
    }


    public Map<TipoComponente, IComponenteFinanceiroLeitura> componentesFinanceiros(){
        return Collections.unmodifiableMap(
                componentesMap().entrySet().stream()
                        .collect(Collectors.toUnmodifiableMap(
                                Map.Entry::getKey,
                                e -> (IComponenteFinanceiroLeitura) e.getValue()
                        ))
        );
    }

    /**
     * Apura e adiciona os encargos por atraso à parcela.
     * Este método encapsula a regra de que encargos só podem ser aplicados
     * a uma parcela que está efetivamente vencida.
     */
    public void apurarEncargosPorAtraso(ValorMonetario jurosDeMora, ValorMonetario multa) {
        // 1. PROTEÇÃO DA INVARIANTE (GUARDA)
        // A Parcela agora protege seu próprio estado.
        if (!(this.estado instanceof EstadoVencida)) {
            throw new IllegalStateException("Encargos por atraso só podem ser apurados para parcelas vencidas.");
        }

        // 2. LÓGICA DE NEGÓCIO INTERNA
        // A Parcela decide como lidar com os novos componentes.
        // Ela pode, por exemplo, mesclar com componentes existentes ou simplesmente adicionar.
        if (jurosDeMora.isPositivo()) {
            // O método privado 'adicionarComponenteInterno' não é exposto publicamente.
            this.adicionarComponenteInterno(new ComponenteFinanceiro(TipoComponente.JUROS_MORA, jurosDeMora));
        }
        if (multa.isPositivo()) {
            this.adicionarComponenteInterno(new ComponenteFinanceiro(TipoComponente.MULTA, multa));
        }

        System.out.println("LOG: Encargos por atraso apurados e adicionados à parcela " + this.parcelaId.Id());
    }

    public void adicionarComponenteInterno(ComponenteFinanceiro componenteFinanceiro) {
        // Usa o mapa para verificar eficientemente se um componente do mesmo tipo já existe.
        ComponenteFinanceiro existente = componentesMap().get(componenteFinanceiro.tipo());

        if (existente != null) {
            // Se já existe, mescla os valores. Não há mudança na estrutura da lista/mapa.
            System.out.println("LOG: Mesclando componente existente do tipo " + componenteFinanceiro.tipo());
            existente.mesclarValor(componenteFinanceiro);
        } else {
            // Se não existe, adiciona à lista e invalida o cache.
            System.out.println("LOG: Adicionando novo componente do tipo " + componenteFinanceiro.tipo());
            this.componentesFinanceiros.add(componenteFinanceiro);

            // PASSO CRUCIAL: Invalida o cache.
            this.componentesMap = null;
        }
    }

    private List<ErroDeValidacao> validarComponentesFinanceiros() {
        return this.componentesFinanceiros.stream()
            .map(ComponenteFinanceiro::validar)
            .filter(Resultado::isErro)
            .map(r -> r.getErro().get())
            .collect(Collectors.toList());
    }

    public Resultado<Parcela, List<ErroDeValidacao>> validarParaAmortizacao() {
        List<ErroDeValidacao> erros = validarComponentesFinanceiros();
        if (!erros.isEmpty()) {
            return Resultado.erro(erros);
        }
        return Resultado.sucesso(this);
    }

    public Object getContrato() {
        return  null;
    }
}
