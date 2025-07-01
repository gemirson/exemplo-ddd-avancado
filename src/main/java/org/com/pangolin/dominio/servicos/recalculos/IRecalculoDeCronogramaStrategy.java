package org.com.pangolin.dominio.servicos.recalculos;

import org.com.pangolin.dominio.parcela.Parcela;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface para estratégias que recalculam um cronograma de parcelas
 * após uma amortização extraordinária de principal.
 */
public interface IRecalculoDeCronogramaStrategy {

    /**
     * Recalcula o cronograma financeiro.
     *
     * @param parcelasAtuais A lista completa de parcelas antes do recálculo.
     * @param numeroParcelaAmortizada O número da parcela que recebeu o pagamento extra.
     * @param saldoPrincipalRemanescente O saldo de principal total da dívida APÓS a amortização extra.
     * @param taxaDeJurosMensal A taxa de juros do contrato.
     * @return Uma nova lista de parcelas representando o cronograma futuro recalculado.
     */
    List<Parcela> recalcular(
            List<Parcela> parcelasAtuais,
            int numeroParcelaAmortizada,
            BigDecimal saldoPrincipalRemanescente,
            BigDecimal taxaDeJurosMensal
    );
}
