package org.com.pangolin.dominio.parcela.estrategias;

import org.com.pangolin.dominio.Carteira;
import org.com.pangolin.dominio.dtos.ParcelaComando;
import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.ParcelaId;
import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;

import org.com.pangolin.dominio.vo.ValorMonetario;

import java.util.List;

// Estratégia para Parcelas Pré-Fixadas
public class EstrategiaCriacaoPreFixada implements IEstrategiaDeCriacaoDeParcela {

    @Override
    public Parcela criar(ParcelaComando comando, int numeroParcela, Carteira contexto) {
        // Esta classe sabe que deve chamar 'new ParcelaPreFixada'.
        List<ComponenteFinanceiro> componentes = construirComponentes(comando); // Método helper

        // A MÁGICA ACONTECE AQUI!
        // Neste exato momento, o construtor da Parcela está em execução. Ele está
        // validando cada parâmetro:
        //  - O ID não é nulo ou negativo?
        //  - A data de vencimento não é no passado?
        //  - A lista de componentes não é nula/vazia?
        //  - A lista de componentes contém os 'COMPONENTES_ESSENCIAIS'?
        //  - Cada um dos ComponenteFinanceiro é válido (verificado pelo validador)?
        //
        // Se QUALQUER uma dessas validações falhar, uma exceção será lançada AQUI,
        // interrompendo a criação desta parcela e, por consequência, a criação
        // de toda a Carteira.
        return new Parcela(
                ParcelaId.de(numeroParcela),
                ValorMonetario.of(comando.valorTotal()),
                comando.dataVencimento(),
                componentes,
                contexto.servicoDeEncargos() // Obtém as dependências do contexto

        );
    }
    // ...
}
