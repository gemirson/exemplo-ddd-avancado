package org.com.pangolin.dominio.parcela.estrategias;

import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.List;

/**
 * Objeto de resultado simples que contém a saída pura do cálculo da estratégia.
 * Não contém IDs ou timestamps.
 */
public record ResultadoDistribuicao(
        List<DetalheAplicacaoComponente> detalhes,
        ValorMonetario valorNaoUtilizado
) {}
