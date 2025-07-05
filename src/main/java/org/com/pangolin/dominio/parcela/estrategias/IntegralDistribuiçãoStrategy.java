package org.com.pangolin.dominio.parcela.estrategias;

import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;
import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.Pagamento;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IntegralDistribuiçãoStrategy  implements  IEstrategiaDeDistribuicaoDePagamento{

    private static final TipoComponente[] ORDEM_PAGAMENTO = {
            TipoComponente.PRINCIPAL,
            TipoComponente.JUROS,
            TipoComponente.MULTA,
            TipoComponente.TAXA
    };
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
        for (TipoComponente tipo : ORDEM_PAGAMENTO) {
            ComponenteFinanceiro componente = (ComponenteFinanceiro) componentes.get(tipo);
            if (componente == null || componente.saldoDevedor().isZero()) continue;

            ValorMonetario saldoAnterior = componente.saldoDevedor();

            // Calcula o valor a ser aplicado
            ValorMonetario valorAplicado = valorRestante.min(saldoAnterior);

            // Atualiza o valor restante do pagamento
            valorRestante = valorRestante.subtrair(valorAplicado);

            // Calcula o novo saldo do componente
            ValorMonetario saldoNovo = saldoAnterior.subtrair(valorAplicado);

            // Cria o "recibo" detalhado para esta operação
            if (valorAplicado.isPositivo()) {
                detalhes.add(new DetalheAplicacaoComponente(
                        tipo,
                        saldoAnterior,
                        valorAplicado,
                        saldoNovo
                ));
            }
        }

        return new ResultadoDistribuicao(detalhes, valorRestante);
    }


}
