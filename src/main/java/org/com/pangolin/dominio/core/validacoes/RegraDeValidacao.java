package org.com.pangolin.dominio.core.validacoes;

import org.com.pangolin.dominio.vo.ErroDeValidacao;

import java.util.Optional;
import java.util.function.Predicate;

public record RegraDeValidacao<T>(
        Predicate<T> condicao,
        ErroDeValidacao erroSeFalhar
) {
    /**
     * Aplica a regra ao objeto.
     * @return Um Optional contendo o erro se a validação falhar, ou Optional.empty() se for bem-sucedida.
     */
    public Optional<ErroDeValidacao> aplicar(T objeto) {
        if (!condicao.test(objeto)) {
            return Optional.of(erroSeFalhar);
        }
        return Optional.empty();
    }
}
