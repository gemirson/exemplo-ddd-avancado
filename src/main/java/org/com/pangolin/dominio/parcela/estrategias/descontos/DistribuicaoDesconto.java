package org.com.pangolin.dominio.parcela.estrategias.descontos;

import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;
import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.IComponenteAmortizacaoHandler;
import org.com.pangolin.dominio.parcela.componentes.descontos.IComponentesDescontoHandler;
import org.com.pangolin.dominio.parcela.estrategias.ResultadoDistribuicao;
import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.*;

public class DistribuicaoDesconto implements IEstrategiaDeDistribuicaoDeDesconto{
    private static final TipoComponente[] ORDEM_DESCONTO  = {
            TipoComponente.JUROS,
            TipoComponente.MULTA,
            TipoComponente.TAXA,
            TipoComponente.PRINCIPAL
    };
    private final Map<TipoComponente, IComponentesDescontoHandler>  registroDeHandlersDesconto;

    public DistribuicaoDesconto() {

        this.registroDeHandlersDesconto = new EnumMap<>(TipoComponente.class);
    }

    /**
     * Método para distribuir o desconto entre as parcelas.
     *
     * @param todosOsComponentes   A lista de componentes financeiros a serem pagos.
     * @param valorTotalDoDesconto Valor do desconto a  ser aplicado sobre os componentes financeiros recebido.
     * @return Um resultado detalhando como os fundos foram distribuídos.
     */
    @Override
    public ResultadoDistribuicao distribuir(Map<TipoComponente, IComponenteFinanceiroLeitura> todosOsComponentes, ValorMonetario valorTotalDoDesconto) {
        ValorMonetario valorRestante = valorTotalDoDesconto;
        List<DetalheAplicacaoComponente> detalhes = new ArrayList<>();

        // ... Lógica para ordenar os componentes ...

        for (TipoComponente tipo : ORDEM_DESCONTO) {

            if (valorRestante.isZero()) break;

            ComponenteFinanceiro componenteAtual = (ComponenteFinanceiro) todosOsComponentes.get(tipo);
            if (tipo == null) continue;

            // 1. Encontra o handler especialista para o tipo atual.
            IComponentesDescontoHandler handler = registroDeHandlersDesconto.get(tipo);
            if (handler == null) continue; // Ou lança exceção para tipo não mapeado

            // 2. Pergunta ao handler se as pré-condições foram satisfeitas.
            if (handler.preCondicoesSatisfeitas(todosOsComponentes)) {
                // 3. Comanda o handler para executar a amortização.
                // Chama o método de cálculo puro
                Optional<DetalheAplicacaoComponente> detalheOpt = handler.calcularPlanoAplicacaoDesconto(
                        componenteAtual,
                        valorRestante,
                        todosOsComponentes
                );

                if (detalheOpt.isPresent()) {
                    DetalheAplicacaoComponente detalhe = detalheOpt.get();
                    detalhes.add(detalhe);
                    // Atualiza o valor restante do pagamento para o próximo handler.
                    valorRestante = valorRestante.subtrair(detalhe.valorAplicado());
                }
            }

        }

        return new ResultadoDistribuicao(detalhes, valorRestante);
    }
}
