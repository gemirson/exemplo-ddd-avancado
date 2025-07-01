package org.com.pangolin.dominio.vo;

import java.time.LocalDate;

/**
 * Representa um pagamento recebido. Um Value Object que carrega mais
 * contexto do que um simples ValorMonetario.
 */
public record Pagamento(
        ValorMonetario valor,
        LocalDate data,
        String metodo // Ex: "PIX", "BOLETO"
) {}
