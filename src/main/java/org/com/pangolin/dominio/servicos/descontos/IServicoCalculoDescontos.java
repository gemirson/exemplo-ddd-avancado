package org.com.pangolin.dominio.servicos.descontos;

import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.vo.ParametrosDesconto;
import org.com.pangolin.dominio.vo.ValorMonetario;

public interface IServicoCalculoDescontos {
    public ValorMonetario calcularDesconto(
            Parcela parcela,
            ParametrosDesconto parametros, // Novo VO, similar ao de encargos
            long diasDeAntecipacao
    );
}
