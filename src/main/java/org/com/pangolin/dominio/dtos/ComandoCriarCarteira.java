package org.com.pangolin.dominio.dtos;

import org.com.pangolin.dominio.enums.TipoProdutoEnum;
import org.com.pangolin.dominio.vo.ParametrosCalculoEncargos;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Comando para criar uma nova Carteira com um cronograma pré-definido.
 * Este é um objeto de dados imutável que carrega a intenção do usuário
 * da camada de aplicação para a camada de domínio.
 */
public record ComandoCriarCarteira(
        UUID id,
        TipoProdutoEnum tipoProduto, // Ex: "PADRAO_PRE_FIXADO", "PREMIUM_PRE_FIXADO", "POS_FIXADO_CDI"
        List<ParcelaComando> parcelas
) {
    /**
     * O construtor canônico do record é o local perfeito para validações
     * de entrada, garantindo que nenhum comando inválido seja criado.
     */
    public ComandoCriarCarteira {
        // Validação de nulidade (Fail-Fast)
        Objects.requireNonNull(id, "O ID da carteira não pode ser nulo.");
        Objects.requireNonNull(tipoProduto, "O tipo de produto não pode ser nulo.");
        Objects.requireNonNull(parcelas, "A lista de parcelas não pode ser nula.");


        if (parcelas.isEmpty()) {
            throw new IllegalArgumentException("É necessário fornecer pelo menos uma parcela para criar a carteira.");
        }
    }

    public ParametrosCalculoEncargos parametros() {
        return null;
    }
}
