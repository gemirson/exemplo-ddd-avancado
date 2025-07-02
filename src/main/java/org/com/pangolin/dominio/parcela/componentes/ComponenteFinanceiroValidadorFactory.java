package org.com.pangolin.dominio.parcela.componentes;

import org.com.pangolin.dominio.core.validacoes.RegraDeValidacao;
import org.com.pangolin.dominio.core.validacoes.Validador;
import org.com.pangolin.dominio.vo.ErroDeValidacao;


import java.util.List;
import java.util.Objects;

public class ComponenteFinanceiroValidadorFactory {

    // As regras são definidas declarativamente como uma lista de objetos.
    // A lista de regras é definida de forma declarativa e imutável.
    private static final List<RegraDeValidacao<ComponenteFinanceiro>> REGRAS = List.of(
            new RegraDeValidacao<>(
                    comp -> Objects.nonNull(comp.tipo()),
                    new ErroDeValidacao("O tipo do componente não pode ser nulo.", "tipo")
            ),
            new RegraDeValidacao<>(
                    comp -> Objects.nonNull(comp.saldoDevedor()),
                    new ErroDeValidacao("O valor original não pode ser nulo.", "valorOriginal")
            ),
            new RegraDeValidacao<>(
                    comp -> comp.saldoDevedor() == null || !comp.saldoDevedor().isNegativo(),
                    new ErroDeValidacao("O valor original não pode ser negativo.", "valorOriginal")
            ),
            new RegraDeValidacao<>(
                    comp -> Objects.nonNull(comp.saldoDevedor()),
                    new ErroDeValidacao("O saldo devedor não pode ser nulo.", "saldoDevedor")
            ),
            new RegraDeValidacao<>(
                    comp -> comp.saldoDevedor() == null || !comp.saldoDevedor().isNegativo(),
                    new ErroDeValidacao("O saldo devedor não pode ser negativo.", "saldoDevedor")
            ),
            new RegraDeValidacao<>(
                    // Regra mais complexa: o saldo devedor não pode ser maior que o original.
                    // Checagens de nulo são feitas primeiro para evitar NullPointerException.
                    comp -> {
                        if (comp.saldoDevedor() == null || comp.saldoDevedor() == null) return true; // Já tratado por outras regras
                        return comp.saldoDevedor().isMenorOuIgualQue(comp.saldoDevedor());
                    },
                    new ErroDeValidacao("O saldo devedor não pode ser maior que o valor original.", "saldoDevedor")
            )
    );

    // O Validador é criado uma única vez com as regras definidas.
    private static final Validador<ComponenteFinanceiro> VALIDADOR = new Validador<>(REGRAS);

    /**
     * @return Uma instância singleton e thread-safe do Validador para ComponenteFinanceiro.
     */
    public static Validador<ComponenteFinanceiro> validador() {
        return VALIDADOR;
    }

}
