package org.com.pangolin.dominio.parcela.estrategias.etapas;

import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;
import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.IComponenteAmortizacaoHandler;
import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.List;
import java.util.Map;

public class AmortizacaoSimplesStep implements IAmortizacaoStep {
    private final TipoComponente tipoAlvo;
    private final IComponenteAmortizacaoHandler handler;

    public AmortizacaoSimplesStep(TipoComponente tipoAlvo, IComponenteAmortizacaoHandler handler) {
        this.tipoAlvo = tipoAlvo;
        this.handler = handler;
    }

    /**
     * Executa a lógica de amortização para esta etapa específica.
     *
     * @param pagamentoRestante O valor do pagamento ainda disponível.
     * @param todosComponentes  O mapa de todos os componentes para contexto.
     * @param detalhes          A lista para registrar os resultados da amortização.
     * @return O novo valor do pagamento restante após a execução da etapa.
     */
    @Override
    public ValorMonetario executar(ValorMonetario pagamentoRestante, Map<TipoComponente,IComponenteFinanceiroLeitura> todosComponentes, List<DetalheAplicacaoComponente> detalhes) {
        if (pagamentoRestante.isZero()) return pagamentoRestante;

        IComponenteFinanceiroLeitura componenteAlvo = (ComponenteFinanceiro) todosComponentes.get(tipoAlvo);
        if (componenteAlvo == null) return pagamentoRestante;

        // Reutiliza toda a lógica de handler que já construímos!
        if (handler.preCondicoesSatisfeitas(todosComponentes)) {
            detalhes.add(handler.calcularAplicacao(componenteAlvo, pagamentoRestante, todosComponentes).get());
        }
        return pagamentoRestante;
    }


}
