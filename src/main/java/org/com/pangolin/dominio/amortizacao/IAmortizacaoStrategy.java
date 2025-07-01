package org.com.pangolin.dominio.amortizacao;

// IAmortizacaoStrategy.java
import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;

import java.math.BigDecimal;
import java.util.List;

public interface IAmortizacaoStrategy {
    void aplicar(List<ComponenteFinanceiro> componentes, BigDecimal valorPago);
}
