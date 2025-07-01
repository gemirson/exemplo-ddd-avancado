package org.com.pangolin.dominio.parcela.estrategias;

import org.com.pangolin.dominio.Carteira;
import org.com.pangolin.dominio.dtos.ParcelaComando;
import org.com.pangolin.dominio.parcela.Parcela;

public class EstrategiaCriacaoPosFixada implements IEstrategiaDeCriacaoDeParcela{
    /**
     * Cria uma instância de Parcela a partir de um comando e do contexto da Carteira.
     *
     * @param comando
     * @param contexto
     */
    @Override
    public Parcela criar(ParcelaComando comando, Carteira contexto) {
        return null;
    }
}
