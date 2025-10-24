package org.com.pangolin.dominio.vo;


import org.com.pangolin.dominio.enums.TipoProdutoEnum;
import org.com.pangolin.dominio.parcela.estados.ContextoTemporal;

import java.util.Objects;

/**
 * Value Object (Record) que atua como uma chave composta para o registro de regras de amortização.
 * A implementação automática de equals() e hashCode() garante a performance O(1) em HashMaps.
 */
public record ChaveDeRegraDeAmortizacao(
        TipoProdutoEnum tipoProduto,
        ContextoTemporal contextoTemporal
) {
    // O construtor canônico garante que a chave nunca seja criada em um estado inválido.
    public ChaveDeRegraDeAmortizacao {
        Objects.requireNonNull(tipoProduto, "Tipo de produto não pode ser nulo na chave.");
        Objects.requireNonNull(contextoTemporal, "Contexto temporal não pode ser nulo na chave.");
    }
}
