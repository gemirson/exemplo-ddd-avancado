package org.com.pangolin.dominio.vo;

/**
 * DTO para carregar o resultado de uma operação de apropriação.
 */
public record ResultadoApropriacao(
        ValorMonetario totalAjustado,
        int parcelasAfetadas
) {}
