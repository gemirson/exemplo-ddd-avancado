package org.com.pangolin.dominio;

import org.com.pangolin.dominio.amortizacao.MemorialDeAmortizacao;
import org.com.pangolin.dominio.core.Entidade;
import org.com.pangolin.dominio.dtos.ParcelaComando;
import org.com.pangolin.dominio.enums.StatusParcelaEnum;
import org.com.pangolin.dominio.model.CarteiraId;
import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.estrategias.IEstrategiaDeCriacaoDeParcela;
import org.com.pangolin.dominio.parcela.estrategias.IEstrategiaDeDistribuicaoDePagamento;
import org.com.pangolin.dominio.servicos.IServicoCalculoEncargos;
import org.com.pangolin.dominio.servicos.recalculos.IRecalculoDeCronogramaStrategy;
import org.com.pangolin.dominio.vo.Pagamento;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Carteira extends Entidade<String, CarteiraId> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final  CarteiraId carteiraId;
    private final List<Parcela> parcelas;


    // --- POLÍTICAS E PARÂMETROS DO CONTRATO (AGORA COMPLETO) ---
    private final IServicoCalculoEncargos servicoEncargos;
    private final IRecalculoDeCronogramaStrategy estrategiaDeRecalculo;
    private final IEstrategiaDeCriacaoDeParcela estrategiaDeCriacaoDeParcela; // NOVA POLÍTICA

    // A ESTRATÉGIA DE DISTRIBUIÇÃO AGORA É UMA POLÍTICA DE PRIMEIRA CLASSE
    private final IEstrategiaDeDistribuicaoDePagamento estrategiaDeDistribuicao;


    /**
     * Construtor protegido para uso interno e fábrica.
     * @param id Identificador único da carteira.
     * @param servicoEncargos Serviço de cálculo de encargos financeiros.
     * @param estrategiaDeRecalculo Estratégia de recalculo do cronograma.
     * @param estrategiaDeDistribuicao Estratégia de distribuição de pagamento.
     */
    protected Carteira(CarteiraId id, IServicoCalculoEncargos servicoEncargos, IRecalculoDeCronogramaStrategy estrategiaDeRecalculo, IEstrategiaDeCriacaoDeParcela estrategiaDeCriacaoDeParcela, IEstrategiaDeDistribuicaoDePagamento estrategiaDeDistribuicao) {
        super(id);
        this.parcelas = new ArrayList<>();
        this.servicoEncargos = servicoEncargos;
        this.estrategiaDeCriacaoDeParcela = estrategiaDeCriacaoDeParcela;
        this.estrategiaDeDistribuicao = estrategiaDeDistribuicao;
        carteiraId = id;
        this.estrategiaDeRecalculo = estrategiaDeRecalculo;
    }



    public  CarteiraId carteiraId() {
        return carteiraId;
    }

    public  List<Parcela> parcelas() {
        return new ArrayList<>(parcelas);
    }

    public  IServicoCalculoEncargos servicoDeEncargos() {
        return servicoEncargos;
    }

    /**
     * Executa a apuração de resultados para todas as parcelas em aberto,
     * calculando a diferença entre seu valor atual (com encargos) e seu valor presente.
     * Este método é idempotente para o mesmo dia.
     *
     * @param dataDeApropriacao A data para a qual a apropriação está sendo feita.
     */
    public void realizarApropriacao(LocalDate dataDeApropriacao){}
    /**
     * Aplica um pagamento a uma única e específica parcela.
     */
    public MemorialDeAmortizacao pagarParcelaUnica(int numeroParcela, Pagamento pagamento,LocalDate dataDeReferencia) {

        // 1. Encontra a entidade filha alvo.
        Parcela parcelaAlvo = obterParcelaPorNumero(numeroParcela);


        // 2. Delega a operação de pagamento para a parcela.
        // Reutilizamos toda a lógica rica que já construímos (estados, distribuição, etc.).
        // USA A ESTRATÉGIA DEFINIDA PARA ESTE CONTRATO, EM VEZ DE CRIAR UMA NOVA.
        MemorialDeAmortizacao memorial = parcelaAlvo.pagar(pagamento, this.estrategiaDeDistribuicao, dataDeReferencia);

        // 3. Inspeciona o resultado para decidir se um recálculo é necessário.
        // A regra de negócio é: se o principal foi amortizado, o cronograma deve ser recalculado.
        boolean principalFoiAmortizado = memorial.detalhes().stream()
                .anyMatch(detalhesPorComponente -> detalhesPorComponente.tipo() == TipoComponente.PRINCIPAL && detalhesPorComponente.valorAplicado().isPositivo());

        if (principalFoiAmortizado) {
            // 4. Se o principal foi amortizado, recalcula o cronograma.
            // A estratégia de recalculo é uma política de negócio que pode ser configurada.
          //  this.estrategiaDeRecalculo.recalcular(this, dataDeReferencia);
        }

        return memorial;
    }


    /**
     * Aplica um pagamento total para quitar um conjunto específico de parcelas.
     * @return Uma lista de memoriais, um para cada parcela paga.
     */
    public List<MemorialDeAmortizacao> pagarMultiplasParcelas(
            List<Integer> numerosDasParcelas,
            Pagamento pagamentoTotal,
            LocalDate dataDeReferencia) {

        // 1. Validação (Regra de Negócio do Agregado)
        List<Parcela> parcelasAlvo = obterParcelasPorNumeros(numerosDasParcelas);

        ValorMonetario somaDosSaldos = parcelasAlvo.stream()
                .map(Parcela::saldoDevedor)
                .reduce(ValorMonetario.ZERO, ValorMonetario::somar);

        // Regra: O pagamento deve ser suficiente para quitar todas as parcelas selecionadas.
        if (pagamentoTotal.valor().isMenorOuIgualQue(somaDosSaldos)) {
            throw new IllegalArgumentException("O valor do pagamento é insuficiente para quitar as parcelas selecionadas.");
        }

        // 2. Orquestração da Aplicação
        List<MemorialDeAmortizacao> memoriais = new ArrayList<>();

        // Para cada parcela alvo, cria um sub-pagamento com o valor exato de seu saldo devedor.
        for (Parcela parcela : parcelasAlvo) {
            // Cria um sub-pagamento para cada parcela com o valor exato de seu saldo.
            Pagamento pagamentoDaParcela = new Pagamento(
                    parcela.saldoDevedor(),
                    pagamentoTotal.data(),
                    pagamentoTotal.metodo()
            );
            memoriais.add(parcela.pagar(pagamentoDaParcela, this.estrategiaDeDistribuicao,dataDeReferencia));
        }

        return memoriais;
    }

    /**
     * Aplica um montante (valor livre) para amortizar sequencialmente as parcelas em
     * aberto, começando pela mais antiga.
     * @param pagamentoMontante O pagamento total a ser distribuído.
     * @return Uma lista de memoriais para cada parcela que foi afetada pelo pagamento.
     */
    public List<MemorialDeAmortizacao> amortizarPorMontante(Pagamento pagamentoMontante, LocalDate dataDeReferencia) {
        List<MemorialDeAmortizacao> memoriais = new ArrayList<>();
        ValorMonetario valorRestanteDoPagamento = pagamentoMontante.valor();


        // 1. Pega todas as parcelas que podem receber pagamento, na ordem correta.
        List<Parcela> parcelasElegiveis = obterParcelasElegiveis();

        // 2. Itera sobre as parcelas, aplicando o pagamento até que ele acabe.
        for (Parcela parcela : parcelasElegiveis) {
            if (valorRestanteDoPagamento.isZero()) {
                break; // O pagamento já foi totalmente utilizado.
            }

            // 3. Calcula o valor a ser aplicado nesta parcela (o menor entre o saldo dela e o restante do pagamento)
            ValorMonetario saldoDaParcela = parcela.valorParcela();
            ValorMonetario valorParaAplicar = valorRestanteDoPagamento.min(saldoDaParcela);

            // 4. Cria um pagamento específico para esta parcela e REUTILIZA a lógica de pagamento unitário.
            Pagamento pagamentoDaParcela = new Pagamento(
                    valorParaAplicar,
                    pagamentoMontante.data(),
                    pagamentoMontante.metodo()
            );

            // A chamada ao `parcela.pagar` já é robusta e lida com estados, componentes, etc.
            MemorialDeAmortizacao memorial = parcela.pagar(pagamentoDaParcela, this.estrategiaDeDistribuicao,dataDeReferencia);
            memoriais.add(memorial);

            // 5. Atualiza o valor restante do pagamento principal.
            valorRestanteDoPagamento = valorRestanteDoPagamento.subtrair(valorParaAplicar);
        }

        // Se sobrar algum valor (troco), o negócio define o que fazer:
        // devolver ao cliente, deixar como crédito, etc. (Lógica omitida).
        if (valorRestanteDoPagamento.isPositivo()) {
            System.out.println("LOG: Valor de troco a ser tratado: " + valorRestanteDoPagamento);
        }

        return memoriais;
    }

    /**
    * Encontra todas as parcelas que estão abertas ou vencidas, ordenadas por número.
     * @return Uma lista de parcela correspondente.
     */
    private List<Parcela> obterParcelasElegiveis() {
        List<Parcela> parcelasElegiveis = this.parcelas.stream()
                .filter(p -> p.status() == StatusParcelaEnum.ABERTA || p.status() == StatusParcelaEnum.VENCIDA)
                .sorted(Comparator.comparingInt(p -> p.parcelaId().Id()))
                .toList();
        return parcelasElegiveis;
    }

    /**
     * Encontra uma parcela específica pelo seu número.
     * @param numeroParcela O número da parcela a ser encontrada.
     * @return A parcela correspondente ao número fornecido.
     */
    private Parcela obterParcelaPorNumero(int numeroParcela) {
        // Aqui você deve implementar a lógica para encontrar a parcela pelo número.
        // Isso pode envolver buscar em uma lista ou mapa de parcelas.
        // Exemplo fictício:
        return this.parcelas.stream()
                .filter(parcela -> parcela.parcelaId().Id().equals(String.valueOf(numeroParcela)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Parcela não encontrada: " + numeroParcela));
    }

    private List<Parcela> obterParcelasPorNumeros(List<Integer> numerosDasParcelas) {
        return numerosDasParcelas.stream()
                .map(this::obterParcelaPorNumero)
                .collect(Collectors.toList());
    }

    // MÉTODO DE NEGÓCIO. A carteira cria seus próprios filhos.
    public void gerarCronogramaAPartirDeComandos(List<ParcelaComando> comandos){

        if (!this.parcelas.isEmpty()) throw new IllegalStateException("Cronograma já gerado.");

        int numeroSequencial = 1; // A sequência começa em 1 para esta carteira.

        for (ParcelaComando cmd : comandos) {
            // A Carteira agora é responsável por atribuir o número sequencial.
            // Delega a criação para a estratégia injetada, passando a si mesma como contexto.
            // Delega a criação para a estratégia, passando o número da parcela como um novo parâmetro.
            Parcela novaParcela = this.estrategiaDeCriacaoDeParcela.criar(
                    cmd,
                    numeroSequencial, // Passando o número sequencial
                    this
            );
            this.parcelas.add(novaParcela);
            numeroSequencial++; // Incrementa o número sequencial para a próxima parcela
        }
    }


    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }


}
