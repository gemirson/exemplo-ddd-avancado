package org.com.pangolin.dominio.parcela.estados;

import org.com.pangolin.dominio.amortizacao.MemorialDeAmortizacao;
import org.com.pangolin.dominio.enums.StatusParcelaEnum;
import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.estrategias.IEstrategiaDeDistribuicaoDeAmortizacao;
import org.com.pangolin.dominio.vo.Pagamento;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.time.LocalDate;

// A interface que todos os estados concretos devem implementar
public interface IEstadoParcela {

    /**
     * Tenta pagar a parcela. A implementação é responsável por todo o fluxo:
     * calcular, aplicar o estado, gerar o memorial e transicionar.
     * @return O memorial da transação bem-sucedida.
     */
    MemorialDeAmortizacao pagar(Parcela parcela,
                                Pagamento pagamento,
                                IEstrategiaDeDistribuicaoDeAmortizacao estrategia,
                                LocalDate dataDeReferencia);
    // ... outros métodos

    /** Tenta cancelar a parcela. */
    void cancelar(Parcela parcela,LocalDate dataDeReferencia);

    /** Tenta estornar o pagamento da parcela. */
    void estornar(Parcela parcela, MemorialDeAmortizacao memorialOriginal);

    /**
     * Calcula e retorna o valor atualizado da parcela, considerando o estado atual.
     * Pode incluir descontos por antecipação ou encargos por atraso.
     * @param parcela O contexto da parcela.
     * @param dataDeReferencia A data para a qual o valor deve ser calculado (normalmente "hoje").
     * @return O ValorMonetario atualizado.
     */
    ValorMonetario valorAtualizado(Parcela parcela, LocalDate dataDeReferencia);

    /** Retorna o valor do Enum correspondente, para persistência e consulta. */
    StatusParcelaEnum status();
}
