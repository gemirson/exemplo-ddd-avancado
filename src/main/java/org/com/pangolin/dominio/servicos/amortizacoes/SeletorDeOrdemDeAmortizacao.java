package org.com.pangolin.dominio.servicos.amortizacoes;

import org.com.pangolin.dominio.comuns.OrdensDeAmortizacao;
import org.com.pangolin.dominio.enums.TipoDistribuicaoAmortizacaoEnum;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.estados.ContextoTemporal;

import java.util.List;
import java.util.Map;

public class SeletorDeOrdemDeAmortizacao {
    // O "livro de regras" do negócio.
    private static final Map<ContextoTemporal, Map<TipoDistribuicaoAmortizacaoEnum,List<TipoComponente>>> REGRAS_DE_ORDEM_AMORTIZACAO= Map.of(
            ContextoTemporal.EM_DIA, Map.of(
                    TipoDistribuicaoAmortizacaoEnum.PARCIAL, OrdensDeAmortizacao.ORDEM_AMORTIZACAO_PARCIAL_EM_DIA,
                    TipoDistribuicaoAmortizacaoEnum.INTEGRAL, OrdensDeAmortizacao.ORDEM_AMORTIZACAO_INTEGRAL_EM_DIA
            ),
            ContextoTemporal.VENCIDA, Map.of(
                    TipoDistribuicaoAmortizacaoEnum.PARCIAL, OrdensDeAmortizacao.ORDEM_AMORTIZACAO_PARCIAL_VENCIDA,
                    TipoDistribuicaoAmortizacaoEnum.INTEGRAL, OrdensDeAmortizacao.ORDEM_AMORTIZACAO_INTEGRAL_VENCIDA
            ),
            ContextoTemporal.ANTECIPADA, Map.of(
                    TipoDistribuicaoAmortizacaoEnum.PARCIAL, OrdensDeAmortizacao.ORDEM_AMORTIZACAO_PARCIAL_ANTECIPADA,
                    TipoDistribuicaoAmortizacaoEnum.INTEGRAL, OrdensDeAmortizacao.ORDEM_AMORTIZACAO_INTEGRAL_ANTECIPADA
            )
    );

    /**
     * Seleciona a ordem de pagamento correta.
     * @param contexto O contexto temporal da operação.
     * @return Um mapa de TipoDistribuicaoAmortizacaoEnum para uma lista de TipoComponente na ordem correta.
     */
    public static Map<TipoDistribuicaoAmortizacaoEnum,List<TipoComponente>> selecionarOrdemAmortizacaoParaContexto(ContextoTemporal contexto) {
        // Busca a ordem de amortização conforme o contexto; se não existir, usa a ordem padrão EM_DIA.
        return REGRAS_DE_ORDEM_AMORTIZACAO.getOrDefault(
                contexto,
                Map.of(
                    TipoDistribuicaoAmortizacaoEnum.PARCIAL, OrdensDeAmortizacao.ORDEM_AMORTIZACAO_PARCIAL_EM_DIA,
                    TipoDistribuicaoAmortizacaoEnum.INTEGRAL, OrdensDeAmortizacao.ORDEM_AMORTIZACAO_INTEGRAL_EM_DIA
                )
        );
    }
}
