package org.com.pangolin.dominio.servicos.descontos;

import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.vo.ParametrosDesconto;
import org.com.pangolin.dominio.vo.Taxa;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.math.BigDecimal;

public class ServicoCalculoDescontos implements  IServicoCalculoDescontos{

    /**
     * Calcula o desconto por antecipação.
     * @param parcela A parcela sobre a qual o desconto será aplicado.
     * @param parametros As regras de desconto do contrato.
     * @param diasDeAntecipacao O número de dias de antecipação, já calculado pela Parcela.
     * @return O valor do desconto a ser concedido.
     */
    public ValorMonetario calcularDesconto(
            Parcela parcela,
            ParametrosDesconto parametros, // Novo VO, similar ao de encargos
            long diasDeAntecipacao
    ) {
        if (diasDeAntecipacao <= 0) {
            return ValorMonetario.ZERO;
        }

        Taxa taxaDesconto = parametros.taxaDeDescontoDiaria();
        ValorMonetario baseDeCalculo = obterSaldoPrincipal(parcela);

        // A lógica de desconto pode ser diferente da de juros (ex: juros simples e não compostos)
        // Valor do Desconto = Base de Cálculo * Taxa Diária * Dias de Antecipação
        ValorMonetario desconto = baseDeCalculo
                .multiplicar(taxaDesconto.comoDecimalDiario())
                .multiplicar(new BigDecimal(diasDeAntecipacao));

        return desconto;
    }

    private ValorMonetario obterSaldoPrincipal(Parcela parcela) {
        return parcela.componentes().stream()
                .filter(c -> c.tipo().equals(TipoComponente.PRINCIPAL))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Componente PRINCIPAL não encontrado"))
                .saldoDevedor();
    }
}
