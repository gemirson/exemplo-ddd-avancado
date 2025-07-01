package org.com.pangolin.dominio.excecoes;

/**
 * Exceção base para todas as falhas relacionadas a violações de regras de negócio do domínio.
 * É uma RuntimeException para evitar o excesso de 'throws' em assinaturas de métodos
 * para falhas que são, em essência, erros de lógica do cliente ou de dados de entrada.
 */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }

    public RegraDeNegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
