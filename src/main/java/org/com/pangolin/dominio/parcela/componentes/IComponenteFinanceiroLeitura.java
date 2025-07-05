package org.com.pangolin.dominio.parcela.componentes;


import org.com.pangolin.dominio.vo.ValorMonetario;

/**
 * Interface que define uma visão de leitura (Read-Only View) de um ComponenteFinanceiro.
 * Expõe apenas os métodos que não modificam o estado, garantindo que os
 * colaboradores (como os Handlers) não possam alterar componentes que não são seu alvo.
 */
public interface IComponenteFinanceiroLeitura {
        TipoComponente tipo();
        ValorMonetario valorOriginal();
        ValorMonetario saldoDevedor();
}
