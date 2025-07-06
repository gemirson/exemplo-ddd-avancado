package org.com.pangolin.dominio.excecoes;

/**
 * Interface que representa um código de erro de negócio estruturado.
 * Garante que todo erro tenha um código único e uma mensagem padrão.
 */
public interface ICodigoDeErro {
    /**
     * @return O código de erro único e estável (ex: "CTE-001").
     */
    String codigo();

    /**
     * @return A mensagem de erro padrão, em português, para fins de log e debug.
     * Pode conter placeholders para formatação (ex: %s, %d).
     */
    String mensagemPadrao();
}
