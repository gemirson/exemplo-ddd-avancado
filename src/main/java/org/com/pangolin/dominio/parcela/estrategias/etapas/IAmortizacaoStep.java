package org.com.pangolin.dominio.parcela.estrategias.etapas;

import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;
import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.List;
import java.util.Map;

/**
 * Representa um único passo executável no processo de distribuição de um pagamento.
 * Variação do Padrão Command.
 */
public interface IAmortizacaoStep {
    /**
     * Executa a lógica de amortização para esta etapa específica.
     * @param pagamentoRestante O valor do pagamento ainda disponível.
     * @param todosComponentes O mapa de todos os componentes para contexto.
     * @param detalhes A lista para registrar os resultados da amortização.
     * @return O novo valor do pagamento restante após a execução da etapa.
     */
    ValorMonetario executar(
            ValorMonetario pagamentoRestante,
            Map<TipoComponente, IComponenteFinanceiroLeitura> todosComponentes,
            List<DetalheAplicacaoComponente> detalhes);
}