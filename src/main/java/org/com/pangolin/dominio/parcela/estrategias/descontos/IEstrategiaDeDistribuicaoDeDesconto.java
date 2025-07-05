package org.com.pangolin.dominio.parcela.estrategias.descontos;

import org.com.pangolin.dominio.parcela.componentes.IComponenteFinanceiroLeitura;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.estrategias.ResultadoDistribuicao;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.Map;

public interface IEstrategiaDeDistribuicaoDeDesconto {

    /**
     * Método para distribuir o desconto entre as parcelas.
     *
     * @param todosOsComponentes A lista de componentes financeiros a serem pagos.
     * @param valorTotalDoDesconto Valor do desconto a  ser aplicado sobre os componentes financeiros recebido.
     * @return Um resultado detalhando como os fundos foram distribuídos.
     */
     ResultadoDistribuicao distribuir(Map<TipoComponente, IComponenteFinanceiroLeitura> todosOsComponentes, ValorMonetario valorTotalDoDesconto);
}
