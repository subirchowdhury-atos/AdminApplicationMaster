package com.adminapplicationmaster.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.adminapplicationmaster.domain.entity.LoanApplication;
import com.adminapplicationmaster.repository.LoanApplicationRepository;
import com.adminapplicationmaster.util.EncryptionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SsnEncryptionMigration {

    private final LoanApplicationRepository loanApplicationRepository;
    private final EncryptionUtil encryptionUtil;

    @Bean
    public CommandLineRunner encryptExistingSSNs() {
        return args -> {
            migrateSSNs();
        };
    }

    @Transactional
    public void migrateSSNs() {
        log.info("Starting SSN encryption migration...");
        
        try {
            // Get all loan applications
            var applications = loanApplicationRepository.findAll();
            int encrypted = 0;
            
            for (LoanApplication app : applications) {
                String ssn = app.getSsn();
                
                // Check if SSN is already encrypted (encrypted SSNs will be longer base64 strings)
                // Plain SSNs are typically 9 digits, encrypted will be much longer
                if (ssn != null && ssn.length() == 9 && ssn.matches("\\d+")) {
                    log.info("Encrypting SSN for application ID: {}", app.getId());
                    
                    // Manually encrypt and update using native query to bypass converter
                    String encryptedSSN = encryptionUtil.encrypt(ssn);
                    
                    // Direct update to avoid triggering the converter again
                    loanApplicationRepository.updateSsnById(app.getId(), encryptedSSN);
                    encrypted++;
                }
            }
            
            log.info("SSN encryption migration completed. Encrypted {} records.", encrypted);
            
        } catch (Exception e) {
            log.error("Error during SSN encryption migration", e);
        }
    }
}