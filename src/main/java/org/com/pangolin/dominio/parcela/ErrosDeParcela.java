package org.com.pangolin.dominio.parcela;

import org.com.pangolin.dominio.excecoes.ICodigoDeErro;

// Códigos de erro relacionados à entidade Parcela
public enum ErrosDeParcela implements ICodigoDeErro {
    PAGAMENTO_INVALIDO_EM_ESTADO_PAGO("PAR-001", "Esta parcela já foi paga."),
    ESTORNO_INVALIDO_EM_ESTADO_ABERTO("PAR-002", "Não é possível estornar uma parcela que não foi paga."),
    DATA_VENCIMENTO_INVALIDA("PAR-003", "A data de vencimento não pode ser no passado: %s.");

    private final String codigo;
    private final String mensagemPadrao;

    ErrosDeParcela(String codigo, String mensagemPadrao) {
        this.codigo = codigo;
        this.mensagemPadrao = mensagemPadrao;
    }

    /**
     * @return O código de erro único e estável (ex: "CTE-001").
     */
    @Override
    public String codigo() {
        return codigo;
    }

    /**
     * @return A mensagem de erro padrão, em português, para fins de log e debug.
     * Pode conter placeholders para formatação (ex: %s, %d).
     */
    @Override
    public String mensagemPadrao() {
        return mensagemPadrao;
    }
}
