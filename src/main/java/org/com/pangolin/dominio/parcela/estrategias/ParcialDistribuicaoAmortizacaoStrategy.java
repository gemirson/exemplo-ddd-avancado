package org.com.pangolin.dominio.parcela.estrategias;

import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;
import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.AmortizacaoComponenteCorrecaoMonetariaHandler;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.AmortizacaoComponenteMoraContabilHandler;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.AmortizacaoComponentePrincipalHandler;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.IComponenteAmortizacaoHandler;
import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.Pagamento;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.*;

public class ParcialDistribuicaoAmortizacaoStrategy implements IEstrategiaDeDistribuicaoDeAmortizacao {

    private static final TipoComponente[] ORDEM_AMORTIZACAO_PARCIAL  = {
            TipoComponente.JUROS,
            TipoComponente.MULTA,
            TipoComponente.TAXA,
            TipoComponente.PRINCIPAL
    };

    /**
     * Mapeia os tipos de componentes para seus respectivos handlers de amortização.
     * Isso permite que a estratégia utilize o handler correto para cada tipo de componente
     * sem precisar de condicionais complexas.
     */

    private final Map<TipoComponente, IComponenteAmortizacaoHandler>  registroDeHandlersAmortizacao;
    private final List<TipoComponente> ordemAmortizacaoParcial;

    public ParcialDistribuicaoAmortizacaoStrategy(List<TipoComponente> ordemAmortizacaoParcial) {
        this.ordemAmortizacaoParcial = ordemAmortizacaoParcial;
        this.registroDeHandlersAmortizacao = new EnumMap<>(TipoComponente.class);

        // Registra os handlers de amortização para cada tipo de componente
        registroDeHandlersAmortizacao.put(TipoComponente.PRINCIPAL,new AmortizacaoComponentePrincipalHandler());
        registroDeHandlersAmortizacao.put(TipoComponente.MORA_CONTABIL, new AmortizacaoComponenteMoraContabilHandler());
        registroDeHandlersAmortizacao.put(TipoComponente.CORRECAO_MONETARIA, new AmortizacaoComponenteCorrecaoMonetariaHandler());
    }

    /**
     * Calcula como um pagamento deve ser distribuído, mas não altera o estado.
     *
     * @param componentes A lista de componentes financeiros a serem pagos.
     * @param pagamento   O pagamento recebido.
     * @return Um resultado detalhando como os fundos foram distribuídos.
     */
    @Override
    public ResultadoDistribuicao calcular(Map<TipoComponente,IComponenteFinanceiroLeitura> componentes, Pagamento pagamento) {
        ValorMonetario valorRestante = pagamento.valor();
        List<DetalheAplicacaoComponente> detalhes = new ArrayList<>();

        // ... Lógica para ordenar os componentes ...

        for (TipoComponente tipo : ordemAmortizacaoParcial) {

            if (valorRestante.isZero()) break;

            ComponenteFinanceiro componenteAtual = (ComponenteFinanceiro) componentes.get(tipo);
            if (tipo == null) continue;

            // 1. Encontra o handler especialista para o tipo atual.
            IComponenteAmortizacaoHandler handler = registroDeHandlersAmortizacao.get(tipo);
            if (handler == null) continue; // Ou lança exceção para tipo não mapeado

            // 2. Pergunta ao handler se as pré-condições foram satisfeitas.
            if (handler.preCondicoesSatisfeitas(componentes)) {
                // 3. Comanda o handler para executar a amortização.
                // Chama o método de cálculo puro
                Optional<DetalheAplicacaoComponente> detalheOpt = handler.calcularAplicacao(
                        componenteAtual,
                        valorRestante,
                        componentes
                );

                if (detalheOpt.isPresent()) {
                    DetalheAplicacaoComponente detalhe = detalheOpt.get();
                    detalhes.add(detalhe);
                    // Atualiza o valor restante do pagamento para o próximo handler.
                    valorRestante = valorRestante.subtrair(detalhe.valorAplicado());
                }
            }

        }

        return new ResultadoDistribuicao(detalhes, valorRestante);
    }



}
