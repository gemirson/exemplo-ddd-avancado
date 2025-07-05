package org.com.pangolin.dominio.parcela.estados;

import org.com.pangolin.dominio.amortizacao.MemorialDeAmortizacao;
import org.com.pangolin.dominio.enums.StatusParcelaEnum;
import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.estrategias.IEstrategiaDeDistribuicaoDePagamento;

import org.com.pangolin.dominio.parcela.estrategias.ResultadoDistribuicao;
import org.com.pangolin.dominio.servicos.IServicoCalculoEncargos;
import org.com.pangolin.dominio.servicos.descontos.IServicoCalculoDescontos;
import org.com.pangolin.dominio.servicos.descontos.ServicoCalculoDescontos;
import org.com.pangolin.dominio.vo.Pagamento;
import org.com.pangolin.dominio.vo.ParametrosDesconto;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.time.LocalDate;

public class EstadoAberta implements  IEstadoParcela{

    private final IServicoCalculoEncargos servicoCalculoEncargos;

    public EstadoAberta(IServicoCalculoEncargos servicoCalculoEncargos) {
        this.servicoCalculoEncargos = servicoCalculoEncargos;

    }


    @Override
    public MemorialDeAmortizacao pagar(
            Parcela parcela,
            Pagamento pagamento,
            IEstrategiaDeDistribuicaoDePagamento estrategia,
            LocalDate dataDeReferencia) {

        // Passo 0: Atuar como guardião. Verificar se o estado real não deveria ser "Vencida".
        if (verificarETransicionarSeVencida(parcela,dataDeReferencia)) {
            // A transição ocorreu. O objeto 'this' (EstadoAberta) não é mais o estado atual da parcela.
            // Re-despachamos a chamada para a parcela, que agora irá delegar para o novo estado (EstadoVencida).
            return parcela.pagar(pagamento, estrategia,dataDeReferencia);
        }

        // 1. O Estado comanda a Estratégia a gerar um "plano de distribuição".
        ResultadoDistribuicao planoDistribuicaoAmortizacao = estrategia.calcular(parcela.componentesFinanceiros(), pagamento);

        // 2. O Estado comanda a PARCELA a APLICAR o plano em si mesma.
        parcela.aplicarPlanoDeDistribuicao(planoDistribuicaoAmortizacao);

        // 3. A Parcela GERA o registro histórico formal
        MemorialDeAmortizacao memorial = parcela.criarMemorial(pagamento, planoDistribuicaoAmortizacao, estrategia.getClass().getSimpleName());

        // 4. TRANSIÇÃO DE ESTADO para PAGA se o saldo for zerado
        if (parcela.saldoDevedor().isZero()) {
            parcela.transicionarPara(new EstadoPaga(servicoCalculoEncargos));
        }
        // 5. RETORNA O MEMORIAL da transação
        return memorial;
    }

    @Override
    public void cancelar(Parcela parcela,LocalDate dataDeReferencia) {
        if (verificarETransicionarSeVencida(parcela,dataDeReferencia)) {
            // Re-despacha a chamada para o novo estado, caso as regras de negócio sejam diferentes.
            parcela.cancelar(dataDeReferencia);
            return;
        }

        System.out.println("LOG: Cancelando Parcela ABERTA...");
        parcela.transicionarPara(new EstadoCancelada());
    }

    @Override
    public void estornar(Parcela parcela, MemorialDeAmortizacao memorialOriginal) {
        // Uma parcela em aberto (não paga) nunca pode ser estornada.
        // Não há necessidade de verificar se está vencida, pois a operação é logicamente impossível.
        throw new IllegalStateException("Não é possível estornar uma parcela que não foi paga.");
    }

    /**
     * Calcula e retorna o valor atualizado da parcela, considerando o estado atual.
     * Pode incluir descontos por antecipação ou encargos por atraso.
     *
     * @param parcela          O contexto da parcela.
     * @param dataDeReferencia A data para a qual o valor deve ser calculado (normalmente "hoje").
     * @return O ValorMonetario atualizado.
     */
    @Override
    public ValorMonetario valorAtualizado(Parcela parcela, LocalDate dataDeReferencia) {
        long diasDeAntecipacao = parcela.diasParaAntecipacao(dataDeReferencia);

        // Se não há antecipação, o valor é o saldo devedor normal.
        if (diasDeAntecipacao <= 0) {
            return parcela.saldoDevedor();
        }

        // Se há antecipação, calculamos o desconto.
        System.out.println("LOG: Calculando desconto para " + diasDeAntecipacao + " dias de antecipação.");

        ParametrosDesconto parametros = null; //parcela.getContrato().getParametrosDeDesconto();
        IServicoCalculoDescontos servicoDesconto = new ServicoCalculoDescontos(); // Pode ser injetado

        ValorMonetario valorDoDesconto = servicoDesconto.calcularDesconto(parcela, parametros, diasDeAntecipacao);

        // Valor atualizado = Saldo Devedor - Desconto
        return parcela.saldoDevedor().subtrair(valorDoDesconto);
    }

    @Override
    public StatusParcelaEnum status() {
        return StatusParcelaEnum.ABERTA;
    }

    /**
     * Lógica privada para verificar a data de vencimento e realizar a transição de estado "just-in-time".
     *
     * @param parcela O contexto da parcela a ser verificado.
     * @return true se a parcela estava vencida e a transição foi realizada, false caso contrário.
     */
    private boolean verificarETransicionarSeVencida(Parcela parcela,LocalDate dataDeReferencia) {
        if (dataDeReferencia.isAfter(parcela.dataVencimento())) {
            System.out.println("LOG: Parcela ABERTA detectada como VENCIDA. Transicionando estado...");
            parcela.transicionarPara(new EstadoVencida(this.servicoCalculoEncargos));
            return true;
        }
        return false;
    }
}
