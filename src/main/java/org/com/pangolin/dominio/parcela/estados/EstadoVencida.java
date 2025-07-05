package org.com.pangolin.dominio.parcela.estados;

import org.com.pangolin.dominio.amortizacao.MemorialDeAmortizacao;
import org.com.pangolin.dominio.enums.StatusParcelaEnum;
import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.estrategias.IEstrategiaDeDistribuicaoDeAmortizacao;
import org.com.pangolin.dominio.parcela.estrategias.ResultadoDistribuicao;
import org.com.pangolin.dominio.servicos.IServicoCalculoEncargos;
import org.com.pangolin.dominio.vo.Pagamento;
import org.com.pangolin.dominio.vo.ParametrosCalculoEncargos;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.time.LocalDate;

public class EstadoVencida implements  IEstadoParcela{

    // Depende da ABSTRAÇÃO, não da implementação.
    private final IServicoCalculoEncargos servicoEncargos;

    public EstadoVencida(IServicoCalculoEncargos servicoEncargos) {
        this.servicoEncargos = servicoEncargos;
    }
    /**
     * Tenta pagar a parcela. A implementação é responsável por todo o fluxo:
     * calcular, aplicar o estado, gerar o memorial e transicionar.
     *
     * @param parcela
     * @param pagamento
     * @param estrategia
     * @return O memorial da transação bem-sucedida.
     */
    @Override
    public MemorialDeAmortizacao pagar(
            Parcela parcela,
            Pagamento pagamento,
            IEstrategiaDeDistribuicaoDeAmortizacao estrategia,
            LocalDate dataDeReferencia) {
        System.out.println("LOG: Pagamento em parcela VENCIDA. Calculando encargos...");
        long diasDeAtraso = parcela.diasDeAtraso(dataDeReferencia);
        // 1. CALCULA E ADICIONA ENCARGOS DE MORA AO ESTADO DA PARCELA
        // A lógica de cálculo pode ser delegada para um serviço de domínio financeiro.

        // Busca os parâmetros do contrato (via parcela -> carteira).
        ParametrosCalculoEncargos parametros = null; // parcela.getContrato().getParametrosDeEncargo();
        // Usa a dependência injetada.
        ValorMonetario juros = servicoEncargos.calcularJuros(parcela,parametros,diasDeAtraso);
        ValorMonetario multa = servicoEncargos.calcularMulta(parcela,parametros,diasDeAtraso);
        // Adiciona os componentes financeiros de juros e multa à parcela
        parcela.apurarEncargosPorAtraso(juros, multa);


        // 1. O Estado comanda a Estratégia a gerar um "plano de distribuição".
        ResultadoDistribuicao planoDistribuicaoAmortizacao = estrategia.calcular(parcela.componentesFinanceiros(), pagamento);

        // 2. O Estado comanda a PARCELA a APLICAR o plano em si mesma.
        parcela.aplicarPlanoDeDistribuicao(planoDistribuicaoAmortizacao);

        // 3. A Parcela GERA o registro histórico formal
        MemorialDeAmortizacao memorial = parcela.criarMemorial(pagamento, planoDistribuicaoAmortizacao, estrategia.getClass().getSimpleName());

        // 4. TRANSIÇÃO DE ESTADO para PAGA se o saldo for zerado
        if (parcela.saldoDevedor().isZero()) {
            parcela.transicionarPara(new EstadoPaga(servicoEncargos));
        }
        // 5. RETORNA O MEMORIAL da transação
        return memorial;
    }

    /**
     * Tenta cancelar a parcela.
     *
     * @param parcela
     */
    @Override
    public void cancelar(Parcela parcela,LocalDate dataDeReferencia) {

    }

    /**
     * Tenta estornar o pagamento da parcela.
     *
     * @param parcela
     * @param memorialOriginal
     */
    @Override
    public void estornar(Parcela parcela, MemorialDeAmortizacao memorialOriginal) {

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
        long diasDeAtraso = parcela.diasDeAtraso(dataDeReferencia);

        System.out.println("LOG: Calculando encargos para " + diasDeAtraso + " dias de atraso.");

        ParametrosCalculoEncargos parametros = null ;

        ValorMonetario juros = servicoEncargos.calcularJuros(parcela, parametros, diasDeAtraso);
        ValorMonetario multa = servicoEncargos.calcularMulta(parcela, parametros, diasDeAtraso);

        // Valor atualizado = Saldo Devedor + Juros + Multa
        return parcela.saldoDevedor().somar(juros).somar(multa);
    }

    /**
     * Retorna o valor do Enum correspondente, para persistência e consulta.
     */
    @Override
    public StatusParcelaEnum status() {
        return StatusParcelaEnum.VENCIDA;
    }
}
