package org.com.pangolin.dominio.core.validacoes;

import org.com.pangolin.dominio.core.Resultado;
import org.com.pangolin.dominio.vo.ErroDeValidacao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Validador<T> {
    private final List<RegraDeValidacao<T>> regras;

    public Validador(List<RegraDeValidacao<T>> regras) {
        this.regras = regras;
    }

    public Resultado<T, List<ErroDeValidacao>> validar(T objeto) {
        List<ErroDeValidacao> erros = this.regras.stream()
                .map(regra -> regra.aplicar(objeto)) // Aplica cada regra
                .filter(Optional::isPresent)        // Filtra apenas os resultados com erro
                .map(Optional::get)                 // Extrai o erro do Optional
                .collect(Collectors.toList());

        if (!erros.isEmpty()) {
            return Resultado.erro(erros);
        }

        return Resultado.sucesso(objeto);
    }
}
