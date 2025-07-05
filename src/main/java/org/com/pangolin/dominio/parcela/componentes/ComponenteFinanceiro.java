package org.com.pangolin.dominio.parcela.componentes;


import org.com.pangolin.dominio.core.Resultado;
import org.com.pangolin.dominio.vo.ErroDeValidacao;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.math.BigDecimal;

public class ComponenteFinanceiro  implements  IComponenteFinanceiroLeitura{
    private final TipoComponente tipo;
    private final ValorMonetario valorOriginal;
    private ValorMonetario saldoDevedor;

    public ComponenteFinanceiro(TipoComponente tipo, ValorMonetario valorOriginal) {
        this.tipo = tipo;
        this.valorOriginal = valorOriginal;
        this.saldoDevedor = valorOriginal;
    }

    public ValorMonetario amortizar(ValorMonetario valor) {
        ValorMonetario valorAmortizado = valor.min(this.saldoDevedor);
        this.saldoDevedor = this.saldoDevedor.subtrair(valorAmortizado);
        return valor.subtrair(valorAmortizado); // Retorna o valor restante do pagamento
    }

    // Getters
    @Override
    public TipoComponente tipo() { return tipo; }
    @Override
    public ValorMonetario valorOriginal() { return valorOriginal;}
    @Override
    public ValorMonetario saldoDevedor() { return saldoDevedor; }

    /**
     * Atualiza o saldo devedor do componente financeiro.
     * Lança uma exceção se o novo saldo for negativo.
     *
     * @param novoSaldo O novo saldo devedor a ser definido.
     */

    public void  atualizarSaldoDevedor(ValorMonetario novoSaldo) {
        if (novoSaldo.isNegativo()) {
            throw new IllegalArgumentException("O saldo devedor não pode ser negativo");
        }
        this.saldoDevedor = novoSaldo;
    }

    public Resultado<ComponenteFinanceiro, ErroDeValidacao> validar() {
        // Exemplo de regra de negócio: o valor original não pode ser negativo
        if (this.valorOriginal.isNegativo()) {
            return Resultado.erro(
                    new ErroDeValidacao("Valor original não pode ser negativo.", "valorOriginal")
            );
        }
        // Poderiam existir outras regras aqui...

        return Resultado.sucesso(this); // Sucesso, retorna a própria instância
    }

    /**
     * Adiciona o saldo devedor de outro componente a este.
     * Usado para mesclar encargos do mesmo tipo.
     */
    public void mesclarValor(ComponenteFinanceiro outro) {
        if (this.tipo != outro.tipo) {
            throw new IllegalArgumentException("Só é possível mesclar componentes do mesmo tipo.");
        }
        // Assume-se que o valor original também deveria ser somado se aplicável.
        // Por simplicidade, vamos focar no saldo devedor.
        this.saldoDevedor = this.saldoDevedor.somar(outro.saldoDevedor());
    }
}

