package org.com.pangolin.dominio.plano.estrategia;

import org.com.pangolin.dominio.enums.TipoApropriacaoEnum;
import org.com.pangolin.dominio.excecoes.RegraDeNegocioException;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Provedor de estratégias de apropriação.
 * Atua como um registro, fornecendo a implementação correta para um
 * TipoApropriacaoEnum, sem usar lógica condicional (if/switch).
 * É configurado na inicialização da aplicação.
 */
public final class ProvedorDeEstrategiaDeApropriacao {

    // O registro de estratégias, injetado no construtor.
    private final Map<TipoApropriacaoEnum, IEstrategiaDeApropriacao> registro;

    public ProvedorDeEstrategiaDeApropriacao(Map<TipoApropriacaoEnum, IEstrategiaDeApropriacao> registro) {
        this.registro = Objects.requireNonNull(registro);
    }

    /**
     * Obtém a estratégia de apropriação para o tipo solicitado.
     * @param tipo O tipo de apropriação desejado.
     * @return A instância da estratégia correspondente.
     * @throws RegraDeNegocioException se nenhuma estratégia for encontrada para o tipo.
     */
    public IEstrategiaDeApropriacao obterEstrategia(TipoApropriacaoEnum tipo) {
        return Optional.ofNullable(registro.get(tipo))
                .orElseThrow(() -> new RegraDeNegocioException(
                        // Usando nosso sistema de erros estruturado
                        ErrosDeNegocio.ESTRATEGIA_NAO_ENCONTRADA, tipo
                ));
    }
}

