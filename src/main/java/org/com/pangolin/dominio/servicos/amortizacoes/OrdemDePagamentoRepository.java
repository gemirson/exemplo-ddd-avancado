package org.com.pangolin.dominio.servicos.amortizacoes;

import org.com.pangolin.dominio.excecoes.RegraDeNegocioException;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.estrategias.OrdemAmortizacaoEnum;
import org.com.pangolin.dominio.parcela.estrategias.etapas.AmortizacaoCompostaPrincipalStepFactory;
import org.com.pangolin.dominio.parcela.estrategias.etapas.AmortizacaoSimplesStepFactory;
import org.com.pangolin.dominio.parcela.estrategias.etapas.IAmortizacaoStepFactory;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Um Serviço de Domínio que atua como um repositório para as "receitas"
 * de ordem de pagamento pré-definidas no sistema.
 */
public class OrdemDePagamentoRepository {

    // O "livro de receitas" completo do sistema.
    private final Map<OrdemAmortizacaoEnum, List<IAmortizacaoStepFactory>> receitas;

    public OrdemDePagamentoRepository() {
        this.receitas = new EnumMap<>(OrdemAmortizacaoEnum.class);

        // Receita 1: Juros Primeiro Simples
        receitas.put(OrdemAmortizacaoEnum.PRIMEIRO_JUROS, List.of(
                new AmortizacaoSimplesStepFactory(TipoComponente.JUROS),
                new AmortizacaoSimplesStepFactory(TipoComponente.PRINCIPAL)
        ));

        // Receita 2: Juros Primeiro com a etapa composta
        receitas.put(OrdemAmortizacaoEnum.PRIMEIRO_PRINCIPAL, List.of(
                new AmortizacaoSimplesStepFactory(TipoComponente.JUROS),
                new AmortizacaoCompostaPrincipalStepFactory()
        ));

        // ... outras receitas ...
    }

    /**
     * Obtém a receita (lista de fábricas de etapa) para uma ordem de amortização específica.
     */
    public List<IAmortizacaoStepFactory> obterReceitaPara(OrdemAmortizacaoEnum ordem) {
        List<IAmortizacaoStepFactory> receita = receitas.get(ordem);
        if (receita == null) {
            // Se a ordem não for encontrada, lança uma exceção de regra de negócio.
            throw new RegraDeNegocioException(ErrosDeNegocio.ORDEM_AMORTIZACAO_INVALIDA, ordem);
        }
        return receita;
    }
}
