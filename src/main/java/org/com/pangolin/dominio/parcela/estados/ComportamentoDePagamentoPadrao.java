package org.com.pangolin.dominio.parcela.estados;

import org.com.pangolin.dominio.amortizacao.MemorialDeAmortizacao;
import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.estrategias.IEstrategiaDeDistribuicaoDeAmortizacao;
import org.com.pangolin.dominio.parcela.estrategias.ResultadoDistribuicao;
import org.com.pangolin.dominio.servicos.IServicoCalculoEncargos;
import org.com.pangolin.dominio.vo.Pagamento;

/**
 * Classe auxiliar (helper) que encapsula a lógica de orquestração de um pagamento,
 * para ser reutilizada por diferentes classes de estado.
 */
class ComportamentoDePagamentoPadrao {

    /**
     * Executa o fluxo padrão de um pagamento: calcula a distribuição, atualiza o estado
     * da parcela, gera o memorial e realiza a transição de estado se necessário.
     */
    public static MemorialDeAmortizacao executar(
            Parcela parcela,
            Pagamento pagamento,
            IEstrategiaDeDistribuicaoDeAmortizacao estrategia,
            IServicoCalculoEncargos servicoCalculoEncargos
    ) {
        // 1. Delega o CÁLCULO para a estratégia
        ResultadoDistribuicao resultadoCalc = estrategia.calcular(parcela.componentesFinanceiros(), pagamento);

        // 2. A Parcela ATUALIZA seu próprio estado com o resultado
        parcela.aplicarResultadoDistribuicao(resultadoCalc);

        // 3. A Parcela GERA o registro histórico formal
        MemorialDeAmortizacao memorial = parcela.criarMemorial(pagamento, resultadoCalc, estrategia.getClass().getSimpleName());

        // 4. TRANSIÇÃO DE ESTADO para PAGA se o saldo for zerado
        if (parcela.saldoDevedor().isZero()) {
            parcela.transicionarPara(new EstadoPaga(servicoCalculoEncargos));
        }
        // 5. RETORNA O MEMORIAL da transação
        return memorial;
    }
}
