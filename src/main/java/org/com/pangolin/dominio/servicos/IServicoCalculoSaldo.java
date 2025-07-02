package org.com.pangolin.dominio.servicos;

import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.time.LocalDate;

/**
 * Interface de Serviço de Domínio.
 * Define o contrato que nosso domínio precisa para obter o saldo atualizado.
 * É completamente agnóstica a qualquer biblioteca externa.
 */
public interface IServicoCalculoSaldo {
    ValorMonetario calcularSaldoAtualizado(Parcela parcela, LocalDate dataDeReferencia);
}

