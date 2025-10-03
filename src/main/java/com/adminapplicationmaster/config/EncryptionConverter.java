package com.adminapplicationmaster.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.adminapplicationmaster.util.EncryptionUtil;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA AttributeConverter for automatic encryption/decryption
 */
@Component
@Converter(autoApply = false) 
public class EncryptionConverter implements AttributeConverter<String, String> {

    private static EncryptionUtil encryptionUtil;

    @Autowired
    public void setEncryptionUtil(EncryptionUtil util) {
        EncryptionConverter.encryptionUtil = util;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        if (encryptionUtil == null) {
            throw new IllegalStateException("EncryptionUtil not initialized. Make sure Spring context is loaded.");
        }
        return encryptionUtil.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        if (encryptionUtil == null) {
            throw new IllegalStateException("EncryptionUtil not initialized. Make sure Spring context is loaded.");
        }
        return encryptionUtil.decrypt(dbData);
    }
}