package org.com.pangolin.dominio.parcela.componentes;

public enum TipoComponente {
    PRINCIPAL                   (1L << 0),  // bit 0 (valor 1)
    JUROS                       (1L << 1),  // bit 1 (valor 2)
    JUROS_MORA                  (1L << 2),  // bit 2 (valor 4)
    MULTA                       (1L << 3),  // bit 3 (valor 8)
    TAXA                        (1L << 4),  // bit 4 (valor 16)
    VALOR_FIXO                  (1L << 5),
    DESCONTO                    (1L << 6),
    VALOR_CONTRATUAL_PARCELA    (1L << 7),
    OUTROS                      (1L << 8),
    MORA_CONTABIL               (1L << 9),
    MORA_JUDICIAL               (1L << 10),
    MORA_CONTRATUAL             (1L << 11),
    CORRECAO_MONETARIA          (1L << 12),
    PRINCIPAL_INCORPORADO       (1L << 13);
    // ... podemos adicionar até 63 componentes no total

    private final long bitValue;

    TipoComponente(long bitValue) {
        this.bitValue = bitValue;
    }

    public long getBitValue() {
        return bitValue;
    }
}
