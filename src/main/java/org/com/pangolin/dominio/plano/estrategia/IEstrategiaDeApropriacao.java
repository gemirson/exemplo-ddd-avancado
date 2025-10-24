package org.com.pangolin.dominio.plano.estrategia;

import org.com.pangolin.dominio.plano.Plano;
import org.com.pangolin.dominio.vo.ResultadoApropriacao;

import java.time.LocalDate;

/**
 * Interface para estratégias que sabem como calcular e aplicar a apropriação
 * em um Plano.
 */
public interface IEstrategiaDeApropriacao {
    ResultadoApropriacao executar(Plano plano, LocalDate dataDeApropriacao);
}