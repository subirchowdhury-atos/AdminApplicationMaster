package com.adminapplicationmaster.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Encryption utility replacing Rails attr_encrypted
 * Handles encryption/decryption of sensitive fields like SSN
 */
@Component
@Slf4j
public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private final SecretKeySpec secretKey;

    public EncryptionUtil(@Value("${encryption.key}") String encryptionKey) {
        // Ensure key is 16, 24, or 32 bytes for AES
        byte[] key = encryptionKey.getBytes(StandardCharsets.UTF_8);
        if (key.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(key, 0, paddedKey, 0, Math.min(key.length, 32));
            key = paddedKey;
        } else if (key.length > 32) {
            byte[] truncatedKey = new byte[32];
            System.arraycopy(key, 0, truncatedKey, 0, 32);
            key = truncatedKey;
        }
        this.secretKey = new SecretKeySpec(key, ALGORITHM);
    }

    public String encrypt(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("Error encrypting data", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return encryptedData;
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error decrypting data", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }
}