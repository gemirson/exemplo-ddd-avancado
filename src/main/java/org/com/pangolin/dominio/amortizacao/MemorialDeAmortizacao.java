package org.com.pangolin.dominio.amortizacao;

import org.com.pangolin.dominio.vo.DetalheAplicacaoComponente;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record MemorialDeAmortizacao(UUID uuid, OffsetDateTime now, ValorMonetario valor, String nomeEstrategia, List<DetalheAplicacaoComponente> detalhes, ValorMonetario subtrair, ValorMonetario valorMonetario) {

}
