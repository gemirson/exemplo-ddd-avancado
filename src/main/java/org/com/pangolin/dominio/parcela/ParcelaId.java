package org.com.pangolin.dominio.parcela;

import org.com.pangolin.dominio.core.EntidadeId;
import org.com.pangolin.dominio.core.validacoes.Validador;
import org.com.pangolin.dominio.vo.ErroDeValidacao;

import java.util.List;
import java.util.stream.Collectors;

public class ParcelaId extends EntidadeId<Integer> {

    protected ParcelaId(Integer id) {
        super(id);
        validarOuLancar();
    }

    private void validarOuLancar() {
        if (Id() == null || Id() <= 0 ) {
            throw new IllegalArgumentException("O valor do ID sequencial deve ser um número positivo.");
        }

    }
    public static ParcelaId de(Integer id) {
        return new ParcelaId(id);
    }
}
