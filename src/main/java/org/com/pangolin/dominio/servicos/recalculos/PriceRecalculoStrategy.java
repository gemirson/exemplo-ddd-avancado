package org.com.pangolin.dominio.servicos.recalculos;

import org.com.pangolin.dominio.enums.StatusParcelaEnum;
import org.com.pangolin.dominio.parcela.Parcela;
import org.com.pangolin.dominio.parcela.componentes.ComponenteFinanceiro;
import org.com.pangolin.dominio.parcela.componentes.TipoComponente;
import org.com.pangolin.dominio.parcela.tipos.ParcelaPreFixada;
import org.com.pangolin.dominio.vo.ValorMonetario;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PriceRecalculoStrategy implements IRecalculoDeCronogramaStrategy {
    @Override
    public List<Parcela> recalcular(List<Parcela> parcelasAtuais, int numeroParcelaAmortizada, BigDecimal saldoPrincipalRemanescente, BigDecimal taxaDeJurosMensal) {
        System.out.println("LOG: Recalculando cronograma futuro usando a Curva Price...");
        List<Parcela> novoCronogramaFuturo = new ArrayList<>();

        // 1. Filtrar as parcelas que precisam ser recalculadas (as futuras)
        List<Parcela> parcelasFuturas = parcelasAtuais.stream()
                .filter(p -> Integer.parseInt(p.Id().Id())  > numeroParcelaAmortizada && p.status() == StatusParcelaEnum.ABERTA)
                .toList();

        int numeroDeParcelasRestantes = parcelasFuturas.size();
        if (numeroDeParcelasRestantes == 0) {
            return List.of(); // Nenhuma parcela futura para recalcular
        }

        // 2. Lógica da Tabela Price: Calcular o novo valor da parcela (PMT)
        // Fórmula: PMT = PV * [i(1+i)^n] / [(1+i)^n - 1]
        BigDecimal i = taxaDeJurosMensal;
        BigDecimal umMaisI = BigDecimal.ONE.add(i);
        BigDecimal umMaisIelevadoAN = umMaisI.pow(numeroDeParcelasRestantes, MathContext.DECIMAL128);

        BigDecimal novoValorParcela = saldoPrincipalRemanescente
                .multiply(i.multiply(umMaisIelevadoAN))
                .divide(umMaisIelevadoAN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        // 3. Gerar as novas parcelas futuras com base no novo PMT
        BigDecimal saldoDevedorAtual = saldoPrincipalRemanescente;
        for (int j = 0; j < numeroDeParcelasRestantes; j++) {
            Parcela parcelaOriginalFutura = parcelasFuturas.get(j);

            BigDecimal jurosDaParcela = saldoDevedorAtual.multiply(i).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalDaParcela = novoValorParcela.subtract(jurosDaParcela);

            List<ComponenteFinanceiro> novosComponentes = List.of(
                    new ComponenteFinanceiro(TipoComponente.PRINCIPAL, ValorMonetario.of(principalDaParcela)),
                    new ComponenteFinanceiro(TipoComponente.JUROS, ValorMonetario.of(jurosDaParcela))
            );

            // Cria uma nova instância de Parcela (ex: ParcelaPreFixada)
            // Assumindo que a classe Parcela tenha um construtor que aceite a lista de componentes
            Parcela novaParcela = ParcelaPreFixada.of(parcelaOriginalFutura.Id(),parcelaOriginalFutura.valorParcela(),parcelaOriginalFutura.dataVencimento());
            novoCronogramaFuturo.add(novaParcela);

            saldoDevedorAtual = saldoDevedorAtual.subtract(principalDaParcela);
        }

        System.out.println("LOG: Cronograma recalculado. Novo valor da parcela: " + novoValorParcela);
        return novoCronogramaFuturo;
    }


}
