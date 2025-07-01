package org.com.pangolin.dominio.model;

import org.com.pangolin.dominio.core.EntidadeId;

/**
 * Representa o identificador de uma carteira.
 *
 * Esta classe estende EntidadeId e é utilizada para identificar de forma única uma carteira
 * dentro do sistema. O identificador é do tipo String e não pode ser nulo ou vazio.
 */
public class CarteiraId extends EntidadeId<String> {

    /**
     * Construtor para CarteiraId.
     *
     * @param id Identificador da carteira, não pode ser nulo ou vazio.
     */
    protected CarteiraId(String id) {
        super(id);
        validate(id);
    }

    private  void validate(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Id não pode ser nulo ou vazio");
        }
    }
    public  static  CarteiraId of(String id) {
        return new CarteiraId(id);
    }
}
