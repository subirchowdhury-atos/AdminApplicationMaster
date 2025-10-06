package com.adminapplicationmaster.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DecisionService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${decision.service.host}")
    private String host;

    @Value("${decision.service.api.token}")
    private String apiToken;

    public DecisionService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<String> getDecision(Object application) {
        try {
            String url = host + "/api/v1/decisions";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Only add token if it exists
            if (apiToken != null && !apiToken.isEmpty()) {
                headers.set("Api-Token", apiToken);
            }

            String requestBody = objectMapper.writeValueAsString(application);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            return restTemplate.postForEntity(url, request, String.class);
        } catch (RestClientException e) {
            log.error("Decision service error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Decision service error");
        } catch (Exception e) {
            log.error("Error processing decision request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Decision service error");
        }
    }
}