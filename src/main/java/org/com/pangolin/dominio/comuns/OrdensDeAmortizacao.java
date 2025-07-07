package org.com.pangolin.dominio.comuns;

import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.estrategias.etapas.AmortizacaoCompostaPrincipalStepFactory;
import org.com.pangolin.dominio.parcela.estrategias.etapas.AmortizacaoSimplesStepFactory;
import org.com.pangolin.dominio.parcela.estrategias.etapas.IAmortizacaoStepFactory;

import java.util.List;

public class OrdensDeAmortizacao {

    public static final List<IAmortizacaoStepFactory> JUROS_PRIMEIRO_COM_PRINCIPAL_COMPOSTO = List.of(
            new AmortizacaoSimplesStepFactory(TipoComponente.JUROS_MORA),
            new AmortizacaoSimplesStepFactory(TipoComponente.MULTA),
            new AmortizacaoSimplesStepFactory(TipoComponente.JUROS),
            new AmortizacaoCompostaPrincipalStepFactory() // A etapa composta é apenas um item na sequência
    );

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
