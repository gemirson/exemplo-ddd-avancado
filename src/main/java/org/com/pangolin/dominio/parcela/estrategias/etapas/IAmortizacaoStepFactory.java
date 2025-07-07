package org.com.pangolin.dominio.parcela.estrategias.etapas;

import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.IComponenteAmortizacaoHandler;

import java.util.Map;

/**
 * Interface para fábricas que sabem como construir uma instância de IAmortizacaoStep.
 * Ela usa o registro de handlers para encontrar os especialistas de que precisa.
 */
@FunctionalInterface
public interface IAmortizacaoStepFactory {
    IAmortizacaoStep criar(Map<TipoComponente, IComponenteAmortizacaoHandler> registroDeHandlers);
}
