package org.com.pangolin.adaptadores.saldo;

import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.servicos.IServicoCalculoSaldo;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.time.LocalDate;

/**
 * ADAPTADOR: Implementa a interface do nosso domínio, mas por baixo dos panos,
 * ele conversa com a biblioteca externa, fazendo a "tradução" necessária.
 * Esta é a nossa Camada Anti-Corrupção.
 */
public class AdaptadorCalculoSaldoExterno implements IServicoCalculoSaldo {


    @Override
    public ValorMonetario calcularSaldoAtualizado(Parcela parcela, LocalDate dataDeReferencia) {
        return null;
    }
}
