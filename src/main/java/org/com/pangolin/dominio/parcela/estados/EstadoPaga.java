package org.com.pangolin.dominio.parcela.estados;

import org.com.pangolin.dominio.amortizacao.MemorialDeAmortizacao;
import org.com.pangolin.dominio.enums.StatusParcelaEnum;
import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.estrategias.IEstrategiaDeDistribuicaoDeAmortizacao;
import org.com.pangolin.dominio.servicos.IServicoCalculoEncargos;
import org.com.pangolin.dominio.vo.Pagamento;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.time.LocalDate;

public class EstadoPaga  implements  IEstadoParcela{

    private final IServicoCalculoEncargos servicoCalculoEncargos;

    public EstadoPaga(IServicoCalculoEncargos servicoCalculoEncargos) {
        this.servicoCalculoEncargos = servicoCalculoEncargos;
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
        throw new IllegalStateException("Esta parcela já foi paga.");
    }

    @Override
    public void cancelar(Parcela parcela,LocalDate dataDeReferencia) {
        // Regra de negócio: não se pode cancelar uma parcela já paga.
        throw new IllegalStateException("Não é possível cancelar uma parcela paga.");
    }

    @Override
    public void estornar(Parcela parcela, MemorialDeAmortizacao memorialOriginal) {
        System.out.println("LOG: Estornando pagamento...");
        parcela.reverterPagamentoInterno(memorialOriginal); // Chama a lógica interna da parcela

        // Decide para qual estado voltar (aberta ou vencida)
        if (LocalDate.now().isAfter(parcela.dataVencimento())) {
            parcela.transicionarPara(new EstadoVencida(this.servicoCalculoEncargos));
        } else {
            parcela.transicionarPara(new EstadoAberta(this.servicoCalculoEncargos));
        }
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
        return ValorMonetario.ZERO;
    }

    @Override
    public StatusParcelaEnum status() {
        return StatusParcelaEnum.PAGA;
    }
}
