package org.com.pangolin.dominio.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Representa um valor monetário como um Value Object imutável.
 * Garante que todas as operações financeiras usem uma escala e
 * modo de arredondamento consistentes.
 */
public final class ValorMonetario implements Comparable<ValorMonetario> {

    // --- CONFIGURAÇÃO CENTRALIZADA ---
    private static final int ESCALA_PADRAO = 2;
    private static final RoundingMode MODO_ARREDONDAMENTO_PADRAO = RoundingMode.HALF_EVEN;
    private static final Locale LOCALIDADE_BRASIL = new Locale("pt", "BR");

    public static final ValorMonetario ZERO = new ValorMonetario(BigDecimal.ZERO);

    private final BigDecimal valor;

    // --- CONSTRUTORES E FÁBRICAS ---
    private ValorMonetario(BigDecimal valor) {
        this.valor = valor.setScale(ESCALA_PADRAO, MODO_ARREDONDAMENTO_PADRAO);
    }

    public static ValorMonetario of(BigDecimal valor) {
        return new ValorMonetario(Objects.requireNonNull(valor));
    }

    public static ValorMonetario of(String valor) {
        return new ValorMonetario(new BigDecimal(Objects.requireNonNull(valor)));
    }

    public static ValorMonetario of(long valor) {
        return new ValorMonetario(BigDecimal.valueOf(valor));
    }


    // --- MÉTODOS ESSENCIAIS (COMPORTAMENTO) ---
    public ValorMonetario somar(ValorMonetario outro) {
        return new ValorMonetario(this.valor.add(outro.valor));
    }

    public ValorMonetario subtrair(ValorMonetario outro) {
        return new ValorMonetario(this.valor.subtract(outro.valor));
    }

    public ValorMonetario multiplicar(BigDecimal fator) {
        return new ValorMonetario(this.valor.multiply(fator));
    }

    public ValorMonetario dividir(BigDecimal divisor) {
        return new ValorMonetario(this.valor.divide(divisor, MODO_ARREDONDAMENTO_PADRAO));
    }

    // --- COMPARAÇÕES E CHECAGENS ---
    public boolean isZero() {
        return this.valor.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositivo() {
        return this.valor.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegativo() {
        return this.valor.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isMaiorQue(ValorMonetario outro) {
        return this.valor.compareTo(outro.valor) > 0;
    }

    public ValorMonetario min(ValorMonetario outro) {
        return this.isMenorOuIgualQue(outro) ? this : outro;
    }

    /**
     * Verifica se este valor monetário é menor ou igual a outro.
     * @param outro O valor a ser comparado.
     * @return true se este valor for menor ou igual ao outro, caso contrário false.
     */
    public boolean isMenorOuIgualQue(ValorMonetario outro) {
        return this.valor.compareTo(outro.valor) <= 0;
    }

    // --- CONTRATO PADRÃO JAVA ---
    @Override
    public int compareTo(ValorMonetario outro) {
        return this.valor.compareTo(outro.valor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValorMonetario that = (ValorMonetario) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return NumberFormat.getCurrencyInstance(LOCALIDADE_BRASIL).format(this.valor);
    }

    // Getter para interoperabilidade com frameworks que possam precisar do valor bruto
    public BigDecimal toBigDecimal() {
        return this.valor;
    }
}
