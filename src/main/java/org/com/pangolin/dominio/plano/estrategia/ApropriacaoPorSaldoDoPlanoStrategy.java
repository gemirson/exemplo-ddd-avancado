package org.com.pangolin.dominio.plano.estrategia;

import org.com.pangolin.dominio.plano.Plano;
import org.com.pangolin.dominio.vo.ResultadoApropriacao;

import java.time.LocalDate;

public class ApropriacaoPorSaldoDoPlanoStrategy implements IEstrategiaDeApropriacao {


    @Override
    public ResultadoApropriacao executar(Plano plano, LocalDate dataDeApropriacao) {
        System.out.println("LOG: Executando apropriação POR SALDO DO PLANO...");
        // Esta estratégia implementaria um algoritmo diferente.
        // Por exemplo, calcularia o VP do fluxo de caixa total do plano
        // e aplicaria o ajuste de forma diferente.
        // ... implementação da lógica de negócio específica ...
        return new ResultadoApropriacao(/*...*/);
    }
}