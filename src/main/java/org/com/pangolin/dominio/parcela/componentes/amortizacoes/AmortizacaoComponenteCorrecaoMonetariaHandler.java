package org.com.pangolin.dominio.parcela.componentes.amortizacoes;

import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.Map;
import java.util.Optional;

public class AmortizacaoComponenteCorrecaoMonetariaHandler implements  IComponenteAmortizacaoHandler{
    /**
     * Verifica se as condições de negócio para amortizar este tipo de componente foram atendidas.
     *
     * @param todosOsComponentes O mapa com todos os componentes da parcela para verificação de contexto.
     * @return true se a amortização for permitida.
     */
    @Override
    public boolean preCondicoesSatisfeitas(Map<TipoComponente, IComponenteFinanceiroLeitura> todosOsComponentes) {
        return false;
    }

    /**
     * CALCULA o resultado da aplicação de um pagamento a um componente,
     * mas NÃO o aplica. Retorna um "plano" de como a amortização deve ocorrer.
     *
     * @param componenteAlvo
     * @param valorPagamento
     * @param contextoDeLeitura
     * @return um Optional contendo o detalhe da aplicação se a amortização ocorrer,
     * ou Optional.empty() se nenhuma parte do pagamento for aplicada.
     */
    @Override
    public Optional<DetalheAplicacaoComponente> calcularAplicacao(IComponenteFinanceiroLeitura componenteAlvo, ValorMonetario valorPagamento, Map<TipoComponente, IComponenteFinanceiroLeitura> contextoDeLeitura) {
        return Optional.empty();
    }
}
