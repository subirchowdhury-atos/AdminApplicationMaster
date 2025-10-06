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

    @Value("${decision.service.api.token:}")
    private String apiToken;

    public DecisionService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<String> getDecision(Object application) {
        try {
            String url = host + "/api/v1/decisions";
            
            log.info("Calling decision service at: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Log token configuration
            log.info("API Token configured: '{}'", apiToken);
            
            // Only add token header if it exists AND is not empty
            // Don't send the header at all if token is null or empty string
            if (apiToken != null && !apiToken.trim().isEmpty()) {
                headers.set("API-TOKEN", apiToken);  // Use uppercase to match interceptor
                log.info("API-TOKEN header added with value: '{}'", apiToken);
            } else {
                log.warn("No API token configured - NOT sending API-TOKEN header");
            }

            String requestBody = objectMapper.writeValueAsString(application);
            log.info("Request payload: {}", requestBody);
            
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            log.info("Decision service response status: {}", response.getStatusCode());
            log.debug("Decision service response body: {}", response.getBody());
            
            return response;
            
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("Decision service HTTP client error - Status: {}, Response: {}", 
                     e.getStatusCode(), e.getResponseBodyAsString());
            log.error("Full exception: ", e);
            return ResponseEntity.status(e.getStatusCode())
                    .body("{\"error\": \"Decision service error: " + e.getMessage() + "\", \"details\": \"" + e.getResponseBodyAsString() + "\"}");
                    
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            log.error("Decision service HTTP server error - Status: {}, Response: {}", 
                     e.getStatusCode(), e.getResponseBodyAsString());
            log.error("Full exception: ", e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("{\"error\": \"Decision service unavailable: " + e.getMessage() + "\"}");
                    
        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.error("Cannot reach decision service at: {}. Is it running?", host, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("{\"error\": \"Cannot connect to decision service. Please ensure the service is running at " + host + "\"}");
                    
        } catch (RestClientException e) {
            log.error("Decision service REST error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Decision service error: " + e.getMessage() + "\"}");
                    
        } catch (Exception e) {
            log.error("Error processing decision request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Decision service error: " + e.getMessage() + "\"}");
        }
    }
}