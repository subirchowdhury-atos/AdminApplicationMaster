package com.adminapplicationmaster.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LocationService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${location.service.host}")
    private String host;

    @Value("${location.service.api.token}")
    private String apiToken;

    public LocationService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<String> getAddressInfo(Object address) {
        try {
            String url = host + "/api/v1/address/eligibility_check";
            
            log.debug("Calling location service at: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Only add token if it exists
            if (apiToken != null && !apiToken.isEmpty()) {
                headers.set("Api-Token", apiToken);
            }

            // Create request body with "address" key
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("address", address);
            
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            log.debug("Request body: {}", requestBodyJson);
            
            HttpEntity<String> request = new HttpEntity<>(requestBodyJson, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            log.debug("Location service response status: {}, body: {}", 
                    response.getStatusCode(), response.getBody());
            
            return response;
            
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // HTTP error responses (4xx, 5xx)
            log.error("Location service HTTP error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
            
        } catch (RestClientException e) {
            // Connection errors, timeouts, etc.
            log.error("Location service connection error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("{\"message\":\"Location service unavailable\"}");
                    
        } catch (Exception e) {
            // JSON processing or other errors
            log.error("Error processing location request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"Error processing location request\"}");
        }
    }
}