package org.com.pangolin.dominio.vo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

/**
 * Value Object imutável e avançado que representa uma taxa financeira
 * com consciência de sua periodicidade.
 * Todas as taxas são normalizadas internamente para uma base diária.
 */
public final class Taxa {
    // Precisão alta para cálculos de juros compostos
    private static final MathContext MC = MathContext.DECIMAL128;
    private static final BigDecimal CEM = new BigDecimal("100");

    // A fonte da verdade: a taxa é sempre armazenada em sua forma diária equivalente.
    private final BigDecimal valorDiarioNormalizado;

    /**
     * Construtor privado. A criação deve ser feita via fábricas estáticas.
     */
    private Taxa(BigDecimal valorDiarioNormalizado) {
        this.valorDiarioNormalizado = valorDiarioNormalizado;
    }

    /**
     * Fábrica principal. Cria uma taxa a partir de um valor e sua periodicidade original.
     * @param valorPercentual O valor em formato percentual (ex: 12 para 12%).
     * @param p A periodicidade original da taxa (DIARIA, MENSAL, ANUAL).
     */
    public static Taxa dePercentual(BigDecimal valorPercentual, Periodicidade p) {
        Objects.requireNonNull(valorPercentual, "Valor percentual não pode ser nulo.");
        Objects.requireNonNull(p, "Periodicidade não pode ser nula.");

        // Converte o % para decimal
        BigDecimal taxaDecimalOriginal = valorPercentual.divide(CEM, MC);

        // Se já for diária, não precisa converter.
        if (p == Periodicidade.DIARIA) {
            return new Taxa(taxaDecimalOriginal);
        }

        // Fórmula de conversão de juros compostos para taxa diária:
        // taxa_diaria = (1 + taxa_periodo)^(1 / dias_no_periodo) - 1
        BigDecimal base = BigDecimal.ONE.add(taxaDecimalOriginal);
        BigDecimal expoente = BigDecimal.ONE.divide(p.diasNoPeriodo(), MC);
        BigDecimal valorDiario = BigDecimal.valueOf(Math.pow(base.doubleValue(), expoente.doubleValue()))
                .subtract(BigDecimal.ONE);

        return new Taxa(valorDiario);
    }

    // --- MÉTODOS DE CONVERSÃO E COMPORTAMENTO ---

    /**
     * Converte a taxa diária interna para sua representação mensal equivalente.
     * @return Uma nova instância de Taxa representando a taxa mensal.
     */
    public Taxa paraMensal() {
        // Fórmula inversa: taxa_mensal = (1 + taxa_diaria)^dias_no_mes - 1
        BigDecimal base = BigDecimal.ONE.add(this.valorDiarioNormalizado);
        BigDecimal expoente = Periodicidade.MENSAL.diasNoPeriodo();
        BigDecimal taxaMensalDecimal = BigDecimal.valueOf(Math.pow(base.doubleValue(), expoente.doubleValue()))
                .subtract(BigDecimal.ONE);

        // Retorna uma nova taxa, que internamente ainda será diária, mas cujo valor foi calculado a partir da mensal.
        // Se quiséssemos manter a periodicidade, teríamos que adicionar um campo 'periodicidade' na classe.
        // A abordagem de normalização é mais pura.
        return new Taxa(taxaMensalDecimal.divide(expoente, MC)); // Normaliza de volta para diária a partir do valor mensal
    }

    // ... métodos paraAnual(), paraSemanal() seguiriam a mesma lógica ...


    // --- MÉTODOS ESSENCIAIS (COMPORTAMENTO DE NEGÓCIO) ---

    /**
     * O comportamento mais importante de uma taxa: ser aplicada a um valor.
     * Calcula o valor resultante da aplicação desta taxa sobre um valor monetário base.
     * Por exemplo, uma taxa de 10% (0.10) aplicada sobre R$ 200,00 retorna R$ 20,00.
     *
     * @param valorBase O ValorMonetario sobre o qual a taxa será aplicada.
     * @return um novo ValorMonetario representando o resultado da aplicação.
     */
    public ValorMonetario aplicarSobre(ValorMonetario valorBase) {
        Objects.requireNonNull(valorBase, "Valor base não pode ser nulo.");

        // Delega a operação de multiplicação para o ValorMonetario,
        // passando o valor decimal bruto desta taxa como o fator.
        return valorBase.multiplicar(this.valorDiarioNormalizado);
    }
    /**
     * O comportamento de negócio mais poderoso: calcula os juros compostos totais
     * sobre um valor base por um determinado número de dias.
     */
    public ValorMonetario jurosCompostosPorPeriodo(ValorMonetario valorBase, long numeroDeDias) {
        if(numeroDeDias <= 0) return ValorMonetario.ZERO;

        // Fórmula: Juros = ValorBase * [(1 + taxa_diaria)^n_dias - 1]
        BigDecimal base = BigDecimal.ONE.add(this.valorDiarioNormalizado);
        BigDecimal fator = BigDecimal.valueOf(Math.pow(base.doubleValue(), numeroDeDias))
                .subtract(BigDecimal.ONE);

        return valorBase.multiplicar(fator);
    }

    public BigDecimal comoDecimalDiario() {
        return this.valorDiarioNormalizado;
    }

    // ... equals, hashCode, toString ...
    @Override
    public String toString() {
        // Exibe a taxa diária para clareza
        return String.format("%.8f%% ao dia", this.valorDiarioNormalizado.multiply(CEM));
    }
}
