package org.com.pangolin.dominio.parcela.estrategias;

import org.com.pangolin.dominio.enums.TipoDistribuicaoAmortizacaoEnum;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;

import java.util.List;

public class ProvedorDeEstrategiaDeDistribuicaoAmortizacoes {

    /**
     * Obtém a estratégia de distribuição correta com base no tipo de pagamento
     * e a configura com a ordem de pagamento necessária.
     *
     * @param tipo A lógica de distribuição a ser usada (PARCIAL, INTEGRAL, etc.).
     * @param ordemDePagamento A ordem de pagamento a ser injetada na estratégia.
     * @return Uma instância da estratégia pronta para ser usada.
     */
    public IEstrategiaDeDistribuicaoDeAmortizacao obterEstrategia(
            TipoDistribuicaoAmortizacaoEnum tipo,
            List<TipoComponente> ordemDePagamento
    ) {
        // A lógica condicional agora vive aqui, isolada e coesa.
        return switch (tipo) {
            case PARCIAL -> new ParcialDistribuicaoAmortizacaoStrategy(ordemDePagamento);
            case INTEGRAL -> new IntegralDistribuicaoAmortizacaoStrategy(ordemDePagamento);
            // Se um novo tipo for adicionado, a modificação ocorre apenas AQUI.
            // default -> throw new IllegalArgumentException("Tipo de pagamento não suportado: " + tipo);
            case LIQUIDACAO -> null;

        };
    }
}
