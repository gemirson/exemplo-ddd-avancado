package org.com.pangolin.dominio.core;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Resultado<S, E> {
    private final S sucesso;
    private final E erro;

    private Resultado(S sucesso, E erro) {
        this.sucesso = sucesso;
        this.erro = erro;
    }

    public static <S, E> Resultado<S, E> sucesso(S valor) {
        return new Resultado<>(valor, null);
    }

    public static <S, E> Resultado<S, E> erro(E erroInfo) {
        return new Resultado<>(null, erroInfo);
    }

    public boolean isSucesso() {
        return sucesso != null;
    }

    public boolean isErro() {
        return erro != null;
    }

    public Optional<S> getValor() {
        return Optional.ofNullable(sucesso);
    }

    public Optional<E> getErro() {
        return Optional.ofNullable(erro);
    }

    // Permite encadear operações no "trilho" do sucesso
    public <R> Resultado<R, E> map(Function<S, R> mapper) {
        if (isSucesso()) {
            return Resultado.sucesso(mapper.apply(sucesso));
        }
        return Resultado.erro(erro);
    }

    // Consumidores para tratar os dois casos, evitando if/else no código cliente
    public Resultado<S, E> ifSucesso(Consumer<S> acao) {
        if (isSucesso()) {
            acao.accept(sucesso);
        }
        return this;
    }

    public Resultado<S, E> ifErro(Consumer<E> acao) {
        if (isErro()) {
            acao.accept(erro);
        }
        return this;
    }
}
