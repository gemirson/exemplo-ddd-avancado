package org.com.pangolin.dominio.parcela.componentes;


/**
 * Value Object imutável que representa um escopo de componentes financeiros,
 * implementado com uma máscara de bits para alta performance.
 */
public record MascaraDeComponentes(long mask) {

    public static final MascaraDeComponentes NENHUM = new MascaraDeComponentes(0L);
    public static final MascaraDeComponentes TODOS = new MascaraDeComponentes(~0L); // Todos os bits ligados

    /**
     * Fábrica principal para criar uma máscara a partir de um conjunto de tipos.
     */
    public static MascaraDeComponentes para(TipoComponente... tipos) {
        long mascaraFinal = 0L;
        for (TipoComponente tipo : tipos) {
            mascaraFinal |= tipo.getBitValue(); // Operação bitwise OR para "ligar" os bits
        }
        return new MascaraDeComponentes(mascaraFinal);
    }

    /**
     * Verifica se um componente específico está incluído nesta máscara.
     */
    public boolean contem(TipoComponente tipo) {
        // Operação bitwise AND para checar se o bit está "ligado".
        return (this.mask & tipo.getBitValue()) != 0;
    }

    /**
     * Retorna uma NOVA máscara com o componente adicionado.
     */
    public MascaraDeComponentes adicionar(TipoComponente tipo) {
        return new MascaraDeComponentes(this.mask | tipo.getBitValue());
    }

    /**
     * Retorna uma NOVA máscara com o componente removido.
     */
    public MascaraDeComponentes remover(TipoComponente tipo) {
        // Usa o operador NOT (~) para criar uma máscara de exclusão.
        return new MascaraDeComponentes(this.mask & ~tipo.getBitValue());
    }
}