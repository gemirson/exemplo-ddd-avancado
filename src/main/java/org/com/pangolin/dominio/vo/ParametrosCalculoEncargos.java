package org.com.pangolin.dominio.vo;

import java.math.BigDecimal;

/**
 * Value Object para encapsular os parâmetros contratuais para cálculo de encargos.
 * Estes dados seriam originados da Carteira.
 */
public record ParametrosCalculoEncargos(
        Taxa taxaJurosMoraAoDia,
        TipoMulta tipoMulta,
        ValorMonetario valorMultaFixa,
        Taxa percentualMulta
) {}
