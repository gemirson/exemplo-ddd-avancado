package org.com.pangolin.dominio.vo;

import org.com.pangolin.dominio.parcela.componentes.TipoComponente;


/**
 * Value Object que representa o resultado detalhado da aplicação de um pagamento
 * a um único componente financeiro. É um registro imutável.
 *
 * Este objeto é criado pela estratégia de distribuição e consumido pela
 * entidade Parcela para atualizar seu estado e gerar o memorial.
 */
public record DetalheAplicacaoComponente(
        /**
         * O tipo de componente que foi afetado (ex: PRINCIPAL, JUROS).
         */
        TipoComponente tipo,

        /**
         * O saldo do componente imediatamente antes da aplicação do pagamento.
         */
        ValorMonetario saldoAnterior,

        /**
         * O valor exato que foi debitado do pagamento e aplicado a este componente.
         */
        ValorMonetario valorAplicado,

        /**
         * O saldo do componente imediatamente após a aplicação do pagamento.
         */
        ValorMonetario saldoNovo
) {}
