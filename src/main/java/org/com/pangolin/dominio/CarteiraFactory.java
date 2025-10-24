package org.com.pangolin.dominio;

import org.com.pangolin.dominio.dtos.ComandoCriarCarteira;
import org.com.pangolin.dominio.dtos.ParcelaComando;
import org.com.pangolin.dominio.enums.TipoDistribuicaoAmortizacaoEnum;
import org.com.pangolin.dominio.enums.TipoProdutoEnum;
import org.com.pangolin.dominio.excecoes.RegraDeNegocioException;
import org.com.pangolin.dominio.model.CarteiraId;

import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.ParcelaId;
import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;

import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.AmortizacaoComponenteCorrecaoMonetariaHandler;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.AmortizacaoComponenteMoraContabilHandler;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.AmortizacaoComponentePrincipalHandler;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.IComponenteAmortizacaoHandler;
import org.com.pangolin.dominio.parcela.estrategias.*;

import org.com.pangolin.dominio.servicos.IServicoCalculoEncargos;
import org.com.pangolin.dominio.servicos.ServicoCalculoEncargos;
import org.com.pangolin.dominio.servicos.amortizacoes.OrdemDePagamentoRepository;
import org.com.pangolin.dominio.servicos.recalculos.IRecalculoDeCronogramaStrategy;
import org.com.pangolin.dominio.servicos.recalculos.PriceRecalculoStrategy;
import org.com.pangolin.dominio.vo.ConfiguracaoDeProduto;
import org.com.pangolin.dominio.vo.ParametrosCalculoEncargos;
import org.com.pangolin.dominio.vo.ValorMonetario;


import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CarteiraFactory {

    // As implementações concretas das estratégias podem ser injetadas aqui.
    private final IServicoCalculoEncargos servicoEncargosPadrao;
    private final IRecalculoDeCronogramaStrategy estrategiaRecalculoPrice;
    private final OrdemDePagamentoRepository repositorioDeOrdens;

    // O "Livro de Receitas" - imutável após a construção.
    private final Map<TipoProdutoEnum, ConfiguracaoDeProduto> mapaDeConfiguracoes;
    private final Map<TipoDistribuicaoAmortizacaoEnum, IEstrategiaDeDistribuicaoDeAmortizacao> mapaDeEstrategiasDistribuicao;
    private final Map<TipoComponente, IComponenteAmortizacaoHandler> registroDeHandlers;

    public CarteiraFactory(EstrategiaCriacaoPreFixada stratCriacaoPre,
                           EstrategiaCriacaoPosFixada stratCriacaoPos,
                           PriceRecalculoStrategy stratRecalculoPrice) {

        this.servicoEncargosPadrao = new ServicoCalculoEncargos();
        this.estrategiaRecalculoPrice = new PriceRecalculoStrategy();
        this.repositorioDeOrdens = new OrdemDePagamentoRepository();

        // --- 1. MONTAR O REGISTRO CENTRAL DE HANDLERS ---
        // Este mapa é o único lugar que conhece todos os handlers disponíveis.
        this.registroDeHandlers = new EnumMap<>(TipoComponente.class);
        this.registroDeHandlers.put(TipoComponente.CORRECAO_MONETARIA, new AmortizacaoComponenteCorrecaoMonetariaHandler());
        this.registroDeHandlers.put(TipoComponente.MORA_CONTABIL, new AmortizacaoComponenteMoraContabilHandler());
        this.registroDeHandlers.put(TipoComponente.PRINCIPAL, new AmortizacaoComponentePrincipalHandler());



        // Montamos o livro de receitas uma única vez.
        this.mapaDeConfiguracoes = new EnumMap<>(TipoProdutoEnum.class);
        this.mapaDeEstrategiasDistribuicao = new EnumMap<>(TipoDistribuicaoAmortizacaoEnum.class);


        // Receita 1: Pré-Fixado com distribuição Parcial
        mapaDeConfiguracoes.put(
                TipoProdutoEnum.PRE_FIXADO_DISTRIBUICAO_PARCIAL,
                new ConfiguracaoDeProduto(stratCriacaoPre, stratRecalculoPrice)
        );

        // Receita 2: Pré-Fixado com distribuição Integral
        mapaDeConfiguracoes.put(
                TipoProdutoEnum.PRE_FIXADO_DISTRIBUICAO_INTEGRAL,
                new ConfiguracaoDeProduto(stratCriacaoPre,stratRecalculoPrice)
        );

        // Receita 3: Pós-Fixado com distribuição Parcial
        mapaDeConfiguracoes.put(
                TipoProdutoEnum.POS_FIXADO_DISTRIBUICAO_PARCIAL,
                new ConfiguracaoDeProduto(stratCriacaoPos,  stratRecalculoPrice)
        );

        // Receita 4: Pós-Fixado com distribuição Integral



        // E assim por diante para todas as combinações válidas...
        //...
    }
    public Carteira criarCarteira(ComandoCriarCarteira comando) throws RegraDeNegocioException {
        // =====================================================================
        // VALIDAÇÃO DE PRÉ-CONDIÇÃO DE NEGÓCIO (FAIL-FAST VERDADEIRO)
        // =====================================================================
        if (comando.parcelas().size() > 999) {
            // A falha ocorre no ponto mais cedo possível, com uma mensagem clara
            // sobre a regra de negócio do agregado.
           // throw new RegraDeNegocioException("Um contrato não pode ter mais de 999 parcelas.");
            // Usar uma exceção customizada é ainda melhor que IllegalArgumentException.
        }

        // 1. Consulta a receita para o tipo de produto solicitado.
        ConfiguracaoDeProduto config = mapaDeConfiguracoes.get(comando.tipoProduto());

        // 2. Validação: se não há receita para este tipo, é um produto inválido.
        if (config == null) {
          //  throw new RegraDeNegocioException("Tipo de produto desconhecido ou inválido: " + comando.tipoProduto());
        }

        System.out.println("LOG: Usando configuração para " + comando.tipoProduto());

        // 3. Busca outros parâmetros necessários (ex: de um repo ou do próprio comando).
        ParametrosCalculoEncargos parametros = //...
                comando.parametros() ;

                // 4. Chama o construtor único da Carteira, usando os "ingredientes" da receita.
                Carteira novaCarteira = new Carteira(
                    CarteiraId.of(comando.id().toString()),
                    servicoEncargosPadrao,
                    config.estrategiaDeRecalculo(),
                    config.estrategiaDeCriacao(),
                    repositorioDeOrdens

                );
        // 5. Comanda a carteira para se popular.
        novaCarteira.gerarCronogramaAPartirDeComandos(comando.parcelas());

        return novaCarteira;
    }


    /**
     * Método privado e eficiente que cria uma lista de parcelas pré-fixadas
     * a partir de comandos de parcelas.
     *
     * @param parcelas Lista de comandos de parcelas.
     * @return Lista de parcelas pré-fixadas.
     */
    private  List<Parcela> obterParcelasPreFixadas(
            List<ParcelaComando> parcelas

    ) {
        // Implementação para criar parcelas pré-fixadas
        // Isso pode incluir lógica de inicialização, validação, etc.
        return parcelas.stream()
                .map(parcela->{
                    // 1. Validação de Entrada (Fail-Fast))})
                    if (parcela.componentes() == null || parcela.componentes().isEmpty()) {
                        throw new IllegalArgumentException("A parcela " + parcela.numero() + " deve ter pelo menos um componente.");
                    }

                    // 2. Transformação de DTO para Objeto de Domínio
                    List<ComponenteFinanceiro> componentes = construirComponentesParaParcela(parcela);

                    // 3. Criação da Parcela com os componentes transformados
                    return new Parcela(
                            ParcelaId.de(parcela.numero()),
                            ValorMonetario.of(parcela.valorTotal()),
                            parcela.dataVencimento(),
                            componentes,
                            servicoEncargosPadrao
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Método helper privado e eficiente que você propôs.
     * Responsável por transformar o DTO de uma parcela em uma lista
     * de objetos de domínio ComponenteFinanceiro válidos.
     *
     * @param parcelaCmd O comando com os dados brutos da parcela.
     * @return Uma lista de ComponenteFinanceiro ricos e validados.
     */
    private List<ComponenteFinanceiro> construirComponentesParaParcela(ParcelaComando parcelaCmd) {
        // 1. Validação de Entrada (Fail-Fast)
        if (parcelaCmd.componentes() == null || parcelaCmd.componentes().isEmpty()) {
            throw new IllegalArgumentException("A parcela " + parcelaCmd.numero() + " deve ter pelo menos um componente.");
        }

        // 2. Transformação de DTO para Objeto de Domínio
        List<ComponenteFinanceiro> componentesResultantes = parcelaCmd.componentes().stream()
                .map(compCmd -> new ComponenteFinanceiro(
                        compCmd.tipo(),
                        ValorMonetario.of(compCmd.valor()) // Usa nosso VO para encapsular o valor
                ))
                .collect(Collectors.toList());
        // 3. Validação de Componentes (opcional, mas recomendado)
        return componentesResultantes;
    }





}
