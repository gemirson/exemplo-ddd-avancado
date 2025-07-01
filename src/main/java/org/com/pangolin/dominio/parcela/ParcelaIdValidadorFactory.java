package org.com.pangolin.dominio.parcela;


import org.com.pangolin.dominio.core.validacoes.RegraDeValidacao;
import org.com.pangolin.dominio.core.validacoes.Validador;
import org.com.pangolin.dominio.vo.ErroDeValidacao;

import java.util.List;

public class ParcelaIdValidadorFactory {

    private static final List<RegraDeValidacao<ParcelaId>> REGRAS = List.of(
            new RegraDeValidacao<>(
                    id -> id != null && id.Id()!= null && !id.Id().isBlank(),
                    new ErroDeValidacao("Id da entidade não pode ser nulo ou vazio.", "id")
            ),
            new RegraDeValidacao<>(
                    id -> {
                        if (id == null || id.Id() == null) return false;
                        return id.Id().matches("\\d+");
                    },
                    new ErroDeValidacao("O número da parcela deve conter apenas dígitos numéricos.", "id")
            ),
            new RegraDeValidacao<>(
                    id -> {
                        if (id == null || id.Id() == null) return false;
                        try {
                            int numero = Integer.parseInt(id.Id());
                            return numero >= 0 && numero <= 999;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    },
                    new ErroDeValidacao("O número da parcela deve ser um inteiro entre 0 e 999.", "id")
            )
            // *** NOVA REGRA PODE SER ADICIONADA AQUI SEM ALTERAR NENHUM OUTRO CÓDIGO ***
    );

    private static final Validador<ParcelaId> validador = new Validador<>(REGRAS);

    public static Validador<ParcelaId> validador() {
        return validador;
    }

}
