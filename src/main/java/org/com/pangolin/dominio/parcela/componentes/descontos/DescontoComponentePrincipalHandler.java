package org.com.pangolin.dominio.parcela.componentes.descontos;

import org.com.pangolin.dominio.comuns.FiltrosComponenteFinanceiro;
import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DescontoComponentePrincipalHandler implements  IComponentesDescontoHandler {

    private  final Set<TipoComponente> COMPONENTES_CUSTO = Set.of(
            TipoComponente.PRINCIPAL,
            TipoComponente.PRINCIPAL_INCORPORADO
    );

    /**
     * Verifica se as condições de negócio para desconto este tipo de componente foram atendidas.
     *
     * @param todosOsComponentes O mapa com todos os componentes da parcela para verificação de contexto.
     * @return true se a amortização for permitida.
     */
    @Override
    public boolean preCondicoesSatisfeitas(Map<TipoComponente, IComponenteFinanceiroLeitura> todosOsComponentes) {
        return FiltrosComponenteFinanceiro.algumComponenteCustoQuitadoOuNegativo(
                COMPONENTES_CUSTO, todosOsComponentes);
    }

    /**
     * CALCULA o resultado da aplicação de um desconto a um componente,
     * mas NÃO o aplica. Retorna um "plano" de como a amortização deve ocorrer.
     *
     * @param componenteAlvo
     * @param valorPagamento
     * @param contextoDeLeitura
     * @return um Optional contendo o detalhe da aplicação se a amortização ocorrer,
     * ou Optional.empty() se nenhuma parte do pagamento for aplicada.
     */
    @Override
    public Optional<DetalheAplicacaoComponente> calcularPlanoAplicacaoDesconto(IComponenteFinanceiroLeitura componenteAlvo, ValorMonetario valorPagamento, Map<TipoComponente, IComponenteFinanceiroLeitura> contextoDeLeitura) {
        return Optional.empty();
    }
}
