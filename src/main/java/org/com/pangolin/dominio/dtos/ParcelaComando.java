package org.com.pangolin.dominio.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ParcelaComando(
        int numero,
        LocalDate dataVencimento,
        List<ComponenteComando> componentes,
        BigDecimal valorTotal
) {}
