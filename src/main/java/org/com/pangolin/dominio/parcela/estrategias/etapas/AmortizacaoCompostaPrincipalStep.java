package org.com.pangolin.dominio.parcela.estrategias.etapas;


import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.componentes.amortizacoes.IComponenteAmortizacaoHandler;
import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.List;
import java.util.Map;

public class AmortizacaoCompostaPrincipalStep implements IAmortizacaoStep {
    // Esta etapa pode ter seus próprios handlers para as sub-tarefas.
    private final IComponenteAmortizacaoHandler handlerPrincipal;
    private final IComponenteAmortizacaoHandler handlerPrincipalIncorporado;

    public AmortizacaoCompostaPrincipalStep(IComponenteAmortizacaoHandler handlerPrincipal, IComponenteAmortizacaoHandler handlerPrincipalIncorporado) {
        this.handlerPrincipal = handlerPrincipal;
        this.handlerPrincipalIncorporado = handlerPrincipalIncorporado;
    }

    // ... construtor ...

    @Override
    public ValorMonetario executar(ValorMonetario pagamentoRestante, Map<TipoComponente, IComponenteFinanceiroLeitura> todosComponentes, List<DetalheAplicacaoComponente> detalhes) {
        IComponenteFinanceiroLeitura principal = todosComponentes.get(TipoComponente.PRINCIPAL);
        IComponenteFinanceiroLeitura principalIncorporado = todosComponentes.get(TipoComponente.PRINCIPAL_INCORPORADO);

        if (principal == null || principalIncorporado == null) return pagamentoRestante;

        // Primeiro, verifica as pré-condições para o grupo.
        if (!handlerPrincipal.preCondicoesSatisfeitas(todosComponentes)) {
            return pagamentoRestante;
        }

        System.out.println("LOG: Executando etapa de amortização composta para Principal e Principal Incorporado.");

        // A lógica de negócio de como distribuir o pagamento entre os dois vive aqui.
        // Exemplo: distribuir 70% para o principal e 30% para o incorporado.
        ValorMonetario pagamentoParaEstaEtapa = pagamentoRestante;


        // Amortiza cada parte...
        ValorMonetario trocoPrincipal = handlerPrincipal.calcularAplicacao(principal, pagamentoParaEstaEtapa,todosComponentes).get().saldoNovo();
        ValorMonetario trocoIncorporado = handlerPrincipalIncorporado.calcularAplicacao(principalIncorporado, pagamentoParaEstaEtapa, todosComponentes).get().saldoNovo();

        // Retorna o troco total da operação composta.
        return trocoPrincipal.somar(trocoIncorporado);
    }
}
