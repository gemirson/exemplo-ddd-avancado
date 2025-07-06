package org.com.pangolin.dominio.parcela.componentes.descontos;

import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public interface IComponentesDescontoHandler {
    /**
     * Verifica se as condições de negócio para desconto este tipo de componente foram atendidas.
     * @param todosOsComponentes O mapa com todos os componentes da parcela para verificação de contexto.
     * @return true se a amortização for permitida.
     */
    public boolean preCondicoesSatisfeitas(Map<TipoComponente, IComponenteFinanceiroLeitura> todosOsComponentes);


    /**
     * CALCULA o resultado da aplicação de um pagamento a um componente,
     * mas NÃO o aplica. Retorna um "plano" de como a amortização deve ocorrer.
     *
     * @return um Optional contendo o detalhe da aplicação se a amortização ocorrer,
     * ou Optional.empty() se nenhuma parte do pagamento for aplicada.
     */
    Optional<DetalheAplicacaoComponente> calcularPlanoAplicacaoDesconto(
            IComponenteFinanceiroLeitura componenteAlvo, // Recebe a visão de leitura
            ValorMonetario valorPagamento,
            Map<TipoComponente, IComponenteFinanceiroLeitura> contextoDeLeitura
    );


}
