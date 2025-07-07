package org.com.pangolin.dominio.parcela.estrategias.etapas;

import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.IComponenteAmortizacaoHandler;

import java.util.Map;

public class AmortizacaoCompostaPrincipalStepFactory implements IAmortizacaoStepFactory {

    @Override
    public IAmortizacaoStep criar(Map<TipoComponente, IComponenteAmortizacaoHandler> registroDeHandlers) {
        IComponenteAmortizacaoHandler handlerPrincipal = registroDeHandlers.get(TipoComponente.PRINCIPAL);
        IComponenteAmortizacaoHandler handlerIncorporado = registroDeHandlers.get(TipoComponente.PRINCIPAL_INCORPORADO);

        if (handlerPrincipal == null || handlerIncorporado == null) {
            throw new IllegalStateException("Handlers para a etapa composta de principal não registrados.");
        }

        return new AmortizacaoCompostaPrincipalStep(handlerPrincipal, handlerIncorporado);
    }
}
