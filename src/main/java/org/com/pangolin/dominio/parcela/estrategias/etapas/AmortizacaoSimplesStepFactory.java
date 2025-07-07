package org.com.pangolin.dominio.parcela.estrategias.etapas;

import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.IComponenteAmortizacaoHandler;

import java.util.Map;

public class AmortizacaoSimplesStepFactory implements IAmortizacaoStepFactory {
    private final TipoComponente tipoAlvo;

    public AmortizacaoSimplesStepFactory(TipoComponente tipoAlvo) {
        this.tipoAlvo = tipoAlvo;
    }

    @Override
    public IAmortizacaoStep criar(Map<TipoComponente, IComponenteAmortizacaoHandler> registroDeHandlers) {
        IComponenteAmortizacaoHandler handler = registroDeHandlers.get(tipoAlvo);
        if (handler == null) {
            throw new IllegalStateException("Handler para " + tipoAlvo + " não registrado.");
        }
        return new AmortizacaoSimplesStep(tipoAlvo, handler);
    }
}
