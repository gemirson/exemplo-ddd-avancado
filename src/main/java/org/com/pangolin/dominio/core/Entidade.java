package org.com.pangolin.dominio.core;

public abstract class Entidade<T, ID  extends  EntidadeId<T>> {

    private final ID id;

    protected Entidade(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id  não pode ser nulo");
        }
        this.id = id;
    }

    public ID Id() {
        return id;
    }

    @Override
    public  abstract  boolean equals(Object o) ;

    @Override
    public abstract  int hashCode() ;
}
