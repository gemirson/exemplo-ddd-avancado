package org.com.pangolin.dominio.plano;

import org.com.pangolin.dominio.enums.TipoApropriacaoEnum;
import org.com.pangolin.dominio.plano.estrategia.IEstrategiaDeApropriacao;
import org.com.pangolin.dominio.plano.estrategia.ProvedorDeEstrategiaDeApropriacao;
import org.com.pangolin.dominio.vo.ResultadoApropriacao;

import java.time.LocalDate;

public class Plano {
    // ...
    // O Plano tem uma dependência de um provedor que sabe como construir estratégias.
    private final ProvedorDeEstrategiaDeApropriacao provedorEstrategiasApropriacao; //Politicas para a apropriação

    private LocalDate ultimaAtualizacao;

    protected Plano(ProvedorDeEstrategiaDeApropriacao provedor) {

        this.provedorEstrategiasApropriacao = provedor;
    }

    /**
     * O método de negócio público. Ele recebe a INTENÇÃO e a delega.
     */
    public ResultadoApropriacao apropriar(TipoApropriacaoEnum tipo, LocalDate dataDeApropriacao) {
        // 1. Pede ao provedor a estratégia correta para o tipo de apropriação solicitado.
        IEstrategiaDeApropriacao estrategia = this.provedorEstrategiasApropriacao.obterEstrategia(tipo);

        // 2. Comanda a estratégia a executar a operação, passando a si mesmo como contexto.
        ResultadoApropriacao resultado = estrategia.executar(this, dataDeApropriacao);

        // 3. Atualiza seu próprio estado (ex: data da última apropriação).
        this.setDataUltimaApropriacao(dataDeApropriacao);

        return resultado;
    }

    public  void setDataUltimaApropriacao(LocalDate data){this.ultimaAtualizacao = data;}
    // ...
}
