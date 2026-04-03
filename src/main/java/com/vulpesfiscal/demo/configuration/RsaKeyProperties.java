package com.vulpesfiscal.demo.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rsa")
public record RsaKeyProperties(String privateKey, String publicKey) {}