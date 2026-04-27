package com.vulpesfiscal.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "focusnfe")
public class FocusNfceProperties {

    /** Token de autenticação da API FocusNFE */
    private String token;

    /** URL base: https://homologacao.focusnfe.com.br ou https://api.focusnfe.com.br */
    private String url;

    /** Quando true, simula resposta da FocusNFE sem fazer chamada real (útil para dev sem certificado) */
    private boolean mock = false;
}
