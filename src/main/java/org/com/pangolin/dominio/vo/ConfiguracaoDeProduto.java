package org.com.pangolin.dominio.vo;


import org.com.pangolin.dominio.parcela.estrategias.IEstrategiaDeCriacaoDeParcela;
import org.com.pangolin.dominio.parcela.estrategias.IEstrategiaDeDistribuicaoDePagamento;
import org.com.pangolin.dominio.servicos.recalculos.IRecalculoDeCronogramaStrategy;

/**
 * Um Value Object que representa uma "receita" completa de políticas
 * para um tipo de produto específico.
 */
public record ConfiguracaoDeProduto(
        IEstrategiaDeCriacaoDeParcela estrategiaDeCriacao,
        IEstrategiaDeDistribuicaoDePagamento estrategiaDeDistribuicao,
        IRecalculoDeCronogramaStrategy estrategiaDeRecalculo
        // ... outras políticas futuras podem ser adicionadas aqui ...
) {}
