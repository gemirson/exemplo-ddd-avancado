package org.com.pangolin.dominio.comuns;

import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FiltrosComponenteFinanceiro  {

    public  static boolean algumComponenteCustoQuitadoOuNegativo(Set<TipoComponente> componentes_custo, Map<TipoComponente, IComponenteFinanceiroLeitura> todosOsComponentes ){
        return componentes_custo.stream()
                .map(todosOsComponentes::get)
                .filter(Objects::nonNull)
                .anyMatch(componente-> componente.saldoDevedor().isZero() || componente.saldoDevedor().isNegativo());
    }
}
