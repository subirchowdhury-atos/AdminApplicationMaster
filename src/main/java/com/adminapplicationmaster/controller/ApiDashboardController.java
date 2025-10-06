package com.adminapplicationmaster.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adminapplicationmaster.domain.entity.LoanApplication;
import com.adminapplicationmaster.repository.LoanApplicationRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST API Controller for Dashboard
 * Returns recent loan applications and statistics
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@AllArgsConstructor
@Slf4j
public class ApiDashboardController {

    private final LoanApplicationRepository loanApplicationRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> index() {
        // Get recent loan applications (last 10)
        List<LoanApplication> recentApplications = loanApplicationRepository
                .findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent();
        
        // Calculate statistics
        long totalApplications = loanApplicationRepository.count();
        long pendingCount = loanApplicationRepository.findByStatusIn(
                List.of("pending"), 
                PageRequest.of(0, 1)
        ).getTotalElements();
        long approvedCount = loanApplicationRepository.findByStatusIn(
                List.of("approved"), 
                PageRequest.of(0, 1)
        ).getTotalElements();
        long rejectedCount = loanApplicationRepository.findByStatusIn(
                List.of("rejected"), 
                PageRequest.of(0, 1)
        ).getTotalElements();
        
        Map<String, Object> response = new HashMap<>();
        response.put("recentApplications", recentApplications);
        response.put("statistics", Map.of(
                "total", totalApplications,
                "pending", pendingCount,
                "approved", approvedCount,
                "rejected", rejectedCount
        ));
        
        return ResponseEntity.ok(response);
    }
}