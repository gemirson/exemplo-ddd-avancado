package org.com.pangolin.dominio.vo;

import java.math.BigDecimal;


public enum Periodicidade {
    DIARIA(BigDecimal.ONE),
    SEMANAL(BigDecimal.valueOf(7)),
    MENSAL(new BigDecimal("30.4375")), // Média de dias em um mês (365.25 / 12)
    ANUAL(new BigDecimal("365.25"));

    private final BigDecimal diasNoPeriodo;

    Periodicidade(BigDecimal diasNoPeriodo) {
        this.diasNoPeriodo = diasNoPeriodo;
    }

    public BigDecimal diasNoPeriodo() {
        return diasNoPeriodo;
    }
}
