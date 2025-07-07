package org.com.pangolin.dominio.parcela.estrategias;

import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;
import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.AmortizacaoComponenteCorrecaoMonetariaHandler;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.AmortizacaoComponenteMoraContabilHandler;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.AmortizacaoComponentePrincipalHandler;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.IComponenteAmortizacaoHandler;
import org.com.pangolin.dominio.parcela.estrategias.etapas.AmortizacaoSimplesStep;
import org.com.pangolin.dominio.parcela.estrategias.etapas.IAmortizacaoStep;
import org.com.pangolin.dominio.parcela.estrategias.etapas.IAmortizacaoStepFactory;
import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.Pagamento;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.*;
import java.util.stream.Collectors;

public class IntegralDistribuicaoAmortizacaoStrategy implements IEstrategiaDeDistribuicaoDeAmortizacao {

    private final List<IAmortizacaoStep> sequenciaDeEtapas;
    private final Map<TipoComponente, IComponenteAmortizacaoHandler>  registroDeHandlersAmortizacao;


    public IntegralDistribuicaoAmortizacaoStrategy(
            List<IAmortizacaoStepFactory> receitaDeEtapas,
            Map<TipoComponente, IComponenteAmortizacaoHandler> registroDeHandlers){
        // A lógica de construção das etapas agora reside aqui dentro.
        // Ele usa a ordem para montar sua sequência interna de comandos.
        // A lógica de construção itera sobre as fábricas e manda cada uma criar sua etapa.
        this.sequenciaDeEtapas = receitaDeEtapas.stream()
                .map(fabrica -> fabrica.criar(registroDeHandlers))
                .collect(Collectors.toList());
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

        // O ALGORITMO PRINCIPAL É UMA SIMPLES ITERAÇÃO DE COMANDOS!
        for (IAmortizacaoStep etapa : this.sequenciaDeEtapas) {
            if (valorRestante.isZero()) break;
            valorRestante = etapa.executar(valorRestante, componentes, detalhes);
        }
        return new ResultadoDistribuicao(detalhes, valorRestante);
    }


}
