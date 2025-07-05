package org.com.pangolin.dominio.parcela.estados;

import org.com.pangolin.dominio.amortizacao.MemorialDeAmortizacao;
import org.com.pangolin.dominio.enums.StatusParcelaEnum;
import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.estrategias.IEstrategiaDeDistribuicaoDeAmortizacao;
import org.com.pangolin.dominio.vo.Pagamento;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.time.LocalDate;

public class EstadoCancelada  implements  IEstadoParcela{

    /**
     * Tenta pagar a parcela. Esta operação é inválida para uma parcela cancelada.
     * @throws IllegalStateException Sempre, pois a regra de negócio não permite a operação.
     */
    @Override
    public MemorialDeAmortizacao pagar(Parcela parcela,
                                       Pagamento pagamento,
                                       IEstrategiaDeDistribuicaoDeAmortizacao estrategia,
                                       LocalDate dataDeReferencia) {
        throw new IllegalStateException("Não é possível pagar uma parcela que está cancelada.");
    }

    /**
     * Tenta cancelar a parcela. Esta operação é inválida pois a parcela já está neste estado.
     * @throws IllegalStateException Sempre, para indicar que a operação é redundante e inválida.
     */
    @Override
    public void cancelar(Parcela parcela,LocalDate dataDeReferencia) {
        throw new IllegalStateException("Esta parcela já se encontra cancelada.");
    }

    /**
     * Tenta estornar o pagamento da parcela. Esta operação é logicamente impossível.
     * @throws IllegalStateException Sempre, pois uma parcela cancelada não possui pagamento para estornar.
     */
    @Override
    public void estornar(Parcela parcela, MemorialDeAmortizacao memorialOriginal) {
        throw new IllegalStateException("Não é possível estornar uma parcela cancelada.");
    }

    /**
     * Calcula e retorna o valor atualizado da parcela, considerando o estado atual.
     * Pode incluir descontos por antecipação ou encargos por atraso.
     *
     * @param parcela          O contexto da parcela.
     * @param dataDeReferencia A data para a qual o valor deve ser calculado (normalmente "hoje").
     * @return O ValorMonetario atualizado.
     */
    @Override
    public ValorMonetario valorAtualizado(Parcela parcela, LocalDate dataDeReferencia) {
        return ValorMonetario.ZERO;
    }


    /**
     * Retorna o valor do Enum correspondente a este estado.
     * @return Sempre StatusParcelaEnum.CANCELADA.
     */
    @Override
    public StatusParcelaEnum status() {
        return StatusParcelaEnum.CANCELADA;
    }
}
