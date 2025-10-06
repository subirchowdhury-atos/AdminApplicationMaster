package com.adminapplicationmaster.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adminapplicationmaster.domain.entity.ApplicationDecision;
import com.adminapplicationmaster.domain.entity.LoanApplication;
import com.adminapplicationmaster.repository.ApplicationDecisionRepository;
import com.adminapplicationmaster.repository.LoanApplicationRepository;
import com.adminapplicationmaster.service.DecisionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * API Controller for Loan Application Services
 * Requires JWT authentication
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/application_services")
@Slf4j
public class ApiApplicationServiceController {

    private final LoanApplicationRepository loanApplicationRepository;
    private final ApplicationDecisionRepository applicationDecisionRepository;
    private final DecisionService decisionService;
    private final ObjectMapper objectMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> index(
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<LoanApplication> loanApplicationsPage;
            
            if (status != null && !status.isEmpty()) {
                loanApplicationsPage = loanApplicationRepository.findByStatus(status, pageable);
            } else {
                loanApplicationsPage = loanApplicationRepository.findAll(pageable);
            }
            
            return ResponseEntity.ok(loanApplicationsPage);
        } catch (Exception e) {
            log.error("Error fetching loan applications", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to fetch loan applications"));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody LoanApplication loanApplication) {
        try {
            LoanApplication saved = loanApplicationRepository.save(loanApplication);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error creating loan application", e);
            return ResponseEntity.unprocessableEntity().body(Map.of("errors", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return loanApplicationRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(400)
                        .body(Map.of("message", "Loan application not found")));
    }

    @PutMapping("/{id}")
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                @Valid @RequestBody LoanApplication loanApplication) {
        return loanApplicationRepository.findById(id)
                .<ResponseEntity<?>>map(existing -> {
                    existing.setFirstName(loanApplication.getFirstName());
                    existing.setLastName(loanApplication.getLastName());
                    existing.setDateOfBirth(loanApplication.getDateOfBirth());
                    existing.setEmail(loanApplication.getEmail());
                    existing.setPhone(loanApplication.getPhone());
                    existing.setIncome(loanApplication.getIncome());
                    existing.setIncomeType(loanApplication.getIncomeType());
                    existing.setRequestedLoanAmount(loanApplication.getRequestedLoanAmount());
                    
                    // Only update SSN if it's provided and not masked
                    if (loanApplication.getSsn() != null && 
                        !loanApplication.getSsn().startsWith("X") && 
                        !loanApplication.getSsn().startsWith("*")) {
                        existing.setSsn(loanApplication.getSsn());
                    }
                    
                    // Only update address if provided
                    if (loanApplication.getAddress() != null) {
                        existing.setAddress(loanApplication.getAddress());
                    }
                    // Note: address is NOT updated if null - keeps existing value
                    
                    // Only update status if provided
                    if (loanApplication.getStatus() != null) {
                        existing.setStatus(loanApplication.getStatus());
                    }
                    
                    LoanApplication updated = loanApplicationRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.unprocessableEntity()
                        .body(Map.of("errors", "Loan application not found")));
    }


    @GetMapping("/{id}/decision_check")
    public ResponseEntity<?> decisionCheck(@PathVariable Long id) {
        try {
            LoanApplication loanApplication = loanApplicationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Loan application not found"));

            // Create request payload
            Map<String, Object> requestPayload = createLoanApplicationPayload(loanApplication);
            
            // Call decision service
            ResponseEntity<String> response = decisionService.getDecision(requestPayload);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                
                // Create application decision
                ApplicationDecision decision = ApplicationDecision.builder()
                        .loanApplication(loanApplication)
                        .request(objectMapper.writeValueAsString(requestPayload))
                        .response(response.getBody())
                        .decision(responseBody.get("final_decision").asText())
                        .build();
                
                ApplicationDecision saved = applicationDecisionRepository.save(decision);
                return ResponseEntity.ok(saved);
            } else {
                return ResponseEntity.status(400)
                        .body(Map.of("message", "Decision Service error"));
            }
        } catch (Exception e) {
            log.error("Error in decision check", e);
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Decision Service error"));
        }
    }

    private Map<String, Object> createLoanApplicationPayload(LoanApplication app) {
        Map<String, Object> addressMap = new HashMap<>();
        if (app.getAddress() != null) {
            addressMap.put("street", app.getAddress().getStreet());
            addressMap.put("unit_number", app.getAddress().getUnitNumber());
            addressMap.put("city", app.getAddress().getCity());
            addressMap.put("state", app.getAddress().getState());
            addressMap.put("zip", app.getAddress().getZip());
            addressMap.put("county", app.getAddress().getCounty());
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("application_id", app.getId());
        payload.put("first_name", app.getFirstName());
        payload.put("last_name", app.getLastName());
        payload.put("date_of_birth", app.getDateOfBirth().toString());
        payload.put("ssn", app.getSsn());
        payload.put("email", app.getEmail());
        payload.put("phone", app.getPhone());
        payload.put("income", app.getIncome());
        payload.put("income_type", app.getIncomeType());
        payload.put("requested_loan_amount", app.getRequestedLoanAmount());
        payload.put("address", addressMap);
        
        return payload;
    }
}