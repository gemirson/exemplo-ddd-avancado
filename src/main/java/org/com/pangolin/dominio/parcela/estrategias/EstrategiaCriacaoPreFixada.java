package org.com.pangolin.dominio.parcela.estrategias;

import org.com.pangolin.dominio.Carteira;
import org.com.pangolin.dominio.dtos.ParcelaComando;
import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.ParcelaId;
import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;

import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.List;

// Estratégia para Parcelas Pré-Fixadas
public class EstrategiaCriacaoPreFixada implements IEstrategiaDeCriacaoDeParcela {

    @Override
    public Parcela criar(ParcelaComando comando, int numeroParcela, Carteira contexto) {
        // Esta classe sabe que deve chamar 'new ParcelaPreFixada'.
        List<ComponenteFinanceiro> componentes = construirComponentes(comando); // Método helper

        return new Parcela(
                ParcelaId.de(numeroParcela),
                ValorMonetario.of(comando.valorTotal()),
                comando.dataVencimento(),
                componentes,
                contexto.servicoDeEncargos() // Obtém as dependências do contexto

        );
    }
    // ...
}
