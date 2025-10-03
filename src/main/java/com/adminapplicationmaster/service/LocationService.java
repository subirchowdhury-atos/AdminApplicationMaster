package com.adminapplicationmaster.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class LocationService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${location.service.host}")
    private String host;

    @Value("${location.service.api.token}")
    private String apiToken;

    public ResponseEntity<String> getAddressInfo(Object address) {
        try {
            String url = host + "/api/address/eligibility_check";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Api_Token", apiToken);

            String requestBody = objectMapper.writeValueAsString(address);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            return restTemplate.postForEntity(url, request, String.class);
            
        } catch (RestClientException e) {
            log.error("Location service error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Location service error");
        } catch (Exception e) {
            log.error("Error processing location request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Location service error");
        }
    }
}