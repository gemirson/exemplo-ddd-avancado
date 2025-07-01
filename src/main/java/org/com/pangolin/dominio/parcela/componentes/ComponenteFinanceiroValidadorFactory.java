package org.com.pangolin.dominio.parcela.componentes;

import org.com.pangolin.dominio.core.validacoes.RegraDeValidacao;
import org.com.pangolin.dominio.core.validacoes.Validador;
import org.com.pangolin.dominio.vo.ErroDeValidacao;


import java.util.List;

public class ComponenteFinanceiroValidadorFactory {

    // As regras são definidas declarativamente como uma lista de objetos.
    private static final List<RegraDeValidacao<ComponenteFinanceiro>> REGRAS = List.of(

            new RegraDeValidacao<>(
                    comp -> comp.saldoDevedor().isPositivo(),
                    new ErroDeValidacao("Saldo devedor não pode ser negativo.", "saldoDevedor")
            ),
            new RegraDeValidacao<>(
                    comp -> comp.saldoDevedor().isNegativo(),
                    new ErroDeValidacao("Saldo devedor não pode exceder o valor original.", "saldoDevedor")
            )
            // *** NOVA REGRA PODE SER ADICIONADA AQUI SEM ALTERAR NENHUM OUTRO CÓDIGO ***
            // , new RegraDeValidacao<>( ... )
    );

    private static final Validador<ComponenteFinanceiro> validador = new Validador<>(REGRAS);

    public static Validador<ComponenteFinanceiro> validador() {
        return validador;
    }

}
