package org.com.pangolin.dominio.parcela.componentes.amortizacoes;

import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AmortizacaoComponentePrincipalHandler implements  IComponenteAmortizacaoHandler{

    private static  final Set<TipoComponente> COMP0NENTES_CUSTO = Set.of(
            TipoComponente.PRINCIPAL
    );
    /**
     * Verifica se as condições de negócio para amortizar este tipo de componente foram atendidas.
     *
     * @param todosOsComponentes O mapa com todos os componentes da parcela para verificação de contexto.
     * @return true se a amortização for permitida.
     */
    @Override
    public boolean preCondicoesSatisfeitas(Map<TipoComponente, IComponenteFinanceiroLeitura> todosOsComponentes) {
        return validarComponentesCusto(COMP0NENTES_CUSTO,todosOsComponentes);
    }

    /**
     * CALCULA o resultado da aplicação de um pagamento a um componente,
     * mas NÃO o aplica. Retorna um "plano" de como a amortização deve ocorrer.
     *
     * @param principal
     * @param valorPagamento
     * @param contextoDeLeitura
     * @return um Optional contendo o detalhe da aplicação se a amortização ocorrer,
     * ou Optional.empty() se nenhuma parte do pagamento for aplicada.
     */
    @Override
    public Optional<DetalheAplicacaoComponente> calcularAplicacao(
            IComponenteFinanceiroLeitura principal,
            ValorMonetario valorPagamento, Map<TipoComponente,
            IComponenteFinanceiroLeitura> contextoDeLeitura) {

        ValorMonetario saldoAnterior =  principal.saldoDevedor();
        ValorMonetario valorAplicado = valorPagamento.min(saldoAnterior);

        if (valorAplicado.isZero()) {
            return Optional.empty(); // Nenhum valor a aplicar
        }

        ValorMonetario novoSaldo = saldoAnterior.subtrair(valorAplicado);

        return Optional.of( new DetalheAplicacaoComponente(
                principal.tipo(),
                saldoAnterior,
                valorAplicado,
                novoSaldo));

    }
}
