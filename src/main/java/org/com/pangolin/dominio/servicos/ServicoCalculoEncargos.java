package org.com.pangolin.dominio.servicos;

import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.vo.ParametrosCalculoEncargos;
import org.com.pangolin.dominio.vo.Taxa;
import org.com.pangolin.dominio.vo.ValorMonetario;


/**
 * Serviço de Domínio stateless responsável por calcular juros e multas
 * para parcelas vencidas.
 */
public final class ServicoCalculoEncargos implements IServicoCalculoEncargos {

    /**
     * Calcula os juros de mora com base nos dias de atraso.
     * @param parcelaVencida A parcela que está vencida.
     * @param parametros As regras contratuais para o cálculo.
     * @return O valor dos juros de mora devidos.
     */
    public ValorMonetario calcularJuros(
            Parcela parcelaVencida,
            ParametrosCalculoEncargos parametros,
            long diasDeAtraso // <-- NOVA DEPENDÊNCIA EXPLÍCITA
    ) {
        if (diasDeAtraso <= 0) {
            return ValorMonetario.ZERO;
        }

        Taxa taxaDeJuros = parametros.taxaJurosMoraAoDia();

        ValorMonetario saldoPrincipal = obterSaldoPrincipal(parcelaVencida);
        // A lógica de negócio agora é pura, sem dependências ocultas.
        return taxaDeJuros.jurosCompostosPorPeriodo(saldoPrincipal, diasDeAtraso);
    }

    private ValorMonetario obterSaldoPrincipal(Parcela parcela) {
        return parcela.componentes().stream()
                .filter(c -> c.tipo().equals(TipoComponente.PRINCIPAL))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Componente PRINCIPAL não encontrado"))
                .saldoDevedor();
    }

    /**
     * Calcula a multa por atraso.
     * @param parcelaVencida A parcela que está vencida.
     * @param parametros As regras contratuais para o cálculo.
     * @return O valor da multa devida.
     */
    public ValorMonetario calcularMulta(
            Parcela parcelaVencida,
            ParametrosCalculoEncargos parametros,
            long diasDeAtraso // <-- Recebe a informação, não a calcula
    ) {
        // A responsabilidade de checar se está atrasado agora é do chamador, mas
        // uma guarda defensiva aqui ainda é uma boa prática.
        if (diasDeAtraso <= 0) {
            return ValorMonetario.ZERO;
        }

        return switch (parametros.tipoMulta()) {
            case VALOR_FIXO -> parametros.valorMultaFixa();

            case PERCENTUAL -> {
                // 1. Pede à Parcela a sua base de cálculo, sem conhecer os detalhes internos.
                ValorMonetario baseDeCalculo = parcelaVencida.baseDeCalculoParaMulta();

                // 2. Diz ao objeto Taxa para aplicar seu comportamento sobre a base.
                yield parametros.percentualMulta().aplicarSobre(baseDeCalculo);
            }

            default -> ValorMonetario.ZERO;
        };
    }
}
