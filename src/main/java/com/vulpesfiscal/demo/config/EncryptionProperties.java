package com.vulpesfiscal.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "encryption")
public class EncryptionProperties {

    /**
     * Chave usada para criptografar tokens sensíveis no banco.
     * Em produção, definir via variável de ambiente: ENCRYPTION_SECRET_KEY
     */
    private String secretKey;
}
