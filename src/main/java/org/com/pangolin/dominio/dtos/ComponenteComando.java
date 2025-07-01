package org.com.pangolin.dominio.dtos;

import org.com.pangolin.dominio.parcela.componentes.TipoComponente;

import java.math.BigDecimal;

public record ComponenteComando(
        TipoComponente tipo,
        BigDecimal valor
) {}
