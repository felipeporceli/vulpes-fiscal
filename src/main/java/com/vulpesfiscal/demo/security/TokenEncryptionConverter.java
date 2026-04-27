package com.vulpesfiscal.demo.security;

import com.vulpesfiscal.demo.config.EncryptionProperties;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Converter
@Component
@RequiredArgsConstructor
public class TokenEncryptionConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM    = "AES/GCM/NoPadding";
    private static final int    IV_LENGTH    = 12;
    private static final int    TAG_BITS     = 128;

    private final EncryptionProperties encryptionProperties;

    @Override
    public String convertToDatabaseColumn(String plaintext) {
        if (plaintext == null) return null;
        try {
            SecretKey key = deriveKey();
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[IV_LENGTH + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(ciphertext, 0, combined, IV_LENGTH, ciphertext.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao criptografar token", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String encrypted) {
        if (encrypted == null) return null;
        try {
            SecretKey key = deriveKey();
            byte[] combined = Base64.getDecoder().decode(encrypted);
            byte[] iv         = Arrays.copyOfRange(combined, 0, IV_LENGTH);
            byte[] ciphertext = Arrays.copyOfRange(combined, IV_LENGTH, combined.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));

            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao descriptografar token", e);
        }
    }

    /** Deriva uma chave AES-256 a partir da string de configuração via SHA-256. */
    private SecretKey deriveKey() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(
                encryptionProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, "AES");
    }
}
