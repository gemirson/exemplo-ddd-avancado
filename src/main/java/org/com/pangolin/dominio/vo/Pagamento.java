package org.com.pangolin.dominio.vo;

import org.com.pangolin.dominio.enums.TipoDistribuicaoAmortizacaoEnum;

import java.time.LocalDate;

/**
 * Representa um pagamento recebido. Um Value Object que carrega mais
 * contexto do que um simples ValorMonetario.
 */
public record Pagamento(
        ValorMonetario valor,
        LocalDate data,
        TipoDistribuicaoAmortizacaoEnum tipoDistribuicaoAmortizacao // Ex: "PIX", "BOLETO"
) {}
