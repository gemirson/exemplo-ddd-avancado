package org.com.pangolin.dominio.plano.estrategia;

import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.plano.Plano;
import org.com.pangolin.dominio.vo.ResultadoApropriacao;
import org.com.pangolin.dominio.vo.Taxa;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.time.LocalDate;

public class ApropriacaoPorParcelaStrategy implements IEstrategiaDeApropriacao {


    @Override
    public ResultadoApropriacao executar(Plano plano, LocalDate dataDeApropriacao) {
        System.out.println("LOG: Executando apropriação POR PARCELA...");
        ValorMonetario totalAjustado = ValorMonetario.ZERO;
        int parcelasAfetadas = 0;

        // Reutiliza os serviços e a lógica que já existem.
        IServicoCalculoValorPresente servicoVP = plano.getServicoValorPresente();
        Taxa taxaDesconto = plano.getParametrosDeDesconto().taxaDeDescontoDiaria();

        for (Parcela parcela : plano.getParcelasEmAberto()) {
            ValorMonetario saldoDevedor = parcela.getValorAtualizado(dataDeApropriacao);
            ValorMonetario valorPresente = servicoVP.calcular(parcela, taxaDesconto, dataDeApropriacao);
            ValorMonetario ajuste = saldoDevedor.subtrair(valorPresente);

            if (ajuste.isPositivo()) {
                parcela.registrarAjusteDeApropriacao(ajuste, dataDeApropriacao);
                totalAjustado = totalAjustado.somar(ajuste);
                parcelasAfetadas++;
            }
        }
        return new ResultadoApropriacao(totalAjustado, parcelasAfetadas);
    }
}