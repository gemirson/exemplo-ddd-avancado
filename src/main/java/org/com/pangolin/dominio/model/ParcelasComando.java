package org.com.pangolin.dominio.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ParcelasComando {
    private final BigDecimal Principal;
    private final BigDecimal Juros;
    private final BigDecimal valorParcela;
    private final LocalDate dataVencimento;
    private final String Id;


    public ParcelasComando(BigDecimal principal, BigDecimal juros, LocalDate dataVencimento, String id, BigDecimal valorParcela) {
        Principal = principal;
        Juros = juros;
        this.dataVencimento = dataVencimento;
        Id = id;
        this.valorParcela = valorParcela;
    }
}
