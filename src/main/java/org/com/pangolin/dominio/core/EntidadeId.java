package org.com.pangolin.dominio.core;

public abstract class EntidadeId<T> {

    private final T id;

    protected EntidadeId(T id) {
        this.id = id;
    }

    public T Id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntidadeId<?> that = (EntidadeId<?>) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "EntidadeId{" +
                "id=" + id +
                '}';
    }
}
