package org.com.pangolin.dominio.comuns;

import org.com.pangolin.dominio.parcela.componentes.TipoComponente;

import java.util.List;
import java.util.Map;

public class OrdensAmortizacao {

    public static final List<TipoComponente> ORDEM_AMORTIZACAO_INTEGRAL_EM_DIA = List.of(
            TipoComponente.PRINCIPAL,
            TipoComponente.JUROS,
            TipoComponente.MULTA,
            TipoComponente.TAXA
    );
    public static final List<TipoComponente> ORDEM_AMORTIZACAO_INTEGRAL_VENCIDA = List.of(
            TipoComponente.PRINCIPAL,
            TipoComponente.JUROS,
            TipoComponente.MULTA,
            TipoComponente.TAXA
    );

    public static final List<TipoComponente> ORDEM_AMORTIZACAO_INTEGRAL_ANTECIPADA = List.of(
            TipoComponente.PRINCIPAL,
            TipoComponente.JUROS,
            TipoComponente.MULTA,
            TipoComponente.TAXA
    );

    public static final List<TipoComponente> ORDEM_AMORTIZACAO_PARCIAL_EM_DIA = List.of(
            TipoComponente.PRINCIPAL,
            TipoComponente.JUROS,
            TipoComponente.MULTA,
            TipoComponente.TAXA
    );
    public static final List<TipoComponente> ORDEM_AMORTIZACAO_PARCIAL_VENCIDA = List.of(
            TipoComponente.PRINCIPAL,
            TipoComponente.JUROS,
            TipoComponente.MULTA,
            TipoComponente.TAXA
    );

    public static final List<TipoComponente> ORDEM_AMORTIZACAO_PARCIAL_ANTECIPADA = List.of(
            TipoComponente.PRINCIPAL,
            TipoComponente.JUROS,
            TipoComponente.MULTA,
            TipoComponente.TAXA
    );
}
