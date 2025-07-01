package org.com.pangolin.dominio.parcela.estrategias;

import org.com.pangolin.dominio.Carteira;
import org.com.pangolin.dominio.dtos.ParcelaComando;
import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface para estratégias que sabem como criar um tipo específico de Parcela.
 */
public interface IEstrategiaDeCriacaoDeParcela {

    /**
     * Cria uma instância de Parcela a partir de um comando e do contexto da Carteira.
     */
    Parcela criar(ParcelaComando comando, int numeroParcela, Carteira contexto);
    /**
     * Método helper privado e eficiente que você propôs.
     * Responsável por transformar o DTO de uma parcela em uma lista
     * de objetos de domínio ComponenteFinanceiro válidos.
     *
     * @param parcelaCmd O comando com os dados brutos da parcela.
     * @return Uma lista de ComponenteFinanceiro ricos e validados.
     */
     default List<ComponenteFinanceiro> construirComponentes(ParcelaComando parcelaCmd) {
        // 1. Validação de Entrada (Fail-Fast)
        if (parcelaCmd.componentes() == null || parcelaCmd.componentes().isEmpty()) {
            throw new IllegalArgumentException("A parcela " + parcelaCmd.numero() + " deve ter pelo menos um componente.");
        }

        // 2. Transformação de DTO para Objeto de Domínio
        List<ComponenteFinanceiro> componentesResultantes = parcelaCmd.componentes().stream()
                .map(compCmd -> new ComponenteFinanceiro(
                        compCmd.tipo(),
                        ValorMonetario.of(compCmd.valor()) // Usa nosso VO para encapsular o valor
                ))
                .collect(Collectors.toList());
        // 3. Validação de Componentes (opcional, mas recomendado)
        return componentesResultantes;
     }
}
