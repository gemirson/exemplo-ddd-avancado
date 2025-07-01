package org.com.pangolin.dominio.servicos;

import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.vo.ParametrosCalculoEncargos;
import org.com.pangolin.dominio.vo.ValorMonetario;

public interface IServicoCalculoEncargos {
    ValorMonetario calcularJuros(Parcela parcelaVencida,
                                 ParametrosCalculoEncargos parametros,
                                 long diasDeAtraso);
    ValorMonetario calcularMulta(Parcela parcelaVencida,
                                 ParametrosCalculoEncargos parametros,
                                 long diasDeAtraso);
}
