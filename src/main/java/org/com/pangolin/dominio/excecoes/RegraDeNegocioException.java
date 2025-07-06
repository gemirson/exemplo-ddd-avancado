package org.com.pangolin.dominio.excecoes;

/**
 * Exceção base para todas as falhas relacionadas a violações de regras de negócio do domínio.
 * É uma RuntimeException para evitar o excesso de 'throws' em assinaturas de métodos
 * para falhas que são, em essência, erros de lógica do cliente ou de dados de entrada.
 */
public class RegraDeNegocioException extends RuntimeException {

    private final ICodigoDeErro codigoDeErro;

    public RegraDeNegocioException(ICodigoDeErro codigoDeErro, Object... args) {
        // Formata a mensagem padrão com os argumentos fornecidos.
        super(String.format(codigoDeErro.mensagemPadrao(), args));
        this.codigoDeErro = codigoDeErro;
    }

    public RegraDeNegocioException(String mensagem, ICodigoDeErro codigoDeErro) {
        super(mensagem);
        this.codigoDeErro = codigoDeErro;
    }

    public RegraDeNegocioException(String mensagem, Throwable causa, ICodigoDeErro codigoDeErro) {
        super(mensagem, causa);
        this.codigoDeErro = codigoDeErro;
    }

    public ICodigoDeErro codigoDeErro() {
        return codigoDeErro;
    }
}
