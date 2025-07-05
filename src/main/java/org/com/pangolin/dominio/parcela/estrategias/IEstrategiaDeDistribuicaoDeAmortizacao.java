package org.com.pangolin.dominio.parcela.estrategias;

import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;
import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.vo.Pagamento;

import java.util.List;
import java.util.Map;

/**
 * Nova interface para estratégias que calculam a distribuição de um pagamento
 * entre os componentes financeiros de uma parcela.
 */
public interface IEstrategiaDeDistribuicaoDeAmortizacao {

    /**
     * Calcula como um pagamento deve ser distribuído, mas não altera o estado.
     * @param componentes A lista de componentes financeiros a serem pagos.
     * @param pagamento O pagamento recebido.
     * @return Um resultado detalhando como os fundos foram distribuídos.
     */
    ResultadoDistribuicao calcular(Map<TipoComponente,IComponenteFinanceiroLeitura> componentes, Pagamento pagamento);

    /**
     * Método auxiliar para encontrar um componente financeiro específico
     * na lista de componentes, dado seu tipo.
     *
     * @param componentes A lista de componentes financeiros.
     * @param tipo O tipo do componente a ser encontrado.
     * @return O componente financeiro correspondente ao tipo, ou null se não encontrado.
     */
    default ComponenteFinanceiro componenteFinanceiro(List<IComponenteFinanceiroLeitura> componentes, TipoComponente tipo) {
        ComponenteFinanceiro componente = (ComponenteFinanceiro) componentes.stream()
                .filter(c -> c.tipo().equals(tipo))
                .findFirst()
                .orElse(null);
        return componente;
    }
}
