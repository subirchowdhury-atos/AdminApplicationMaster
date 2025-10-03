package com.adminapplicationmaster.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adminapplicationmaster.domain.entity.Address;
import com.adminapplicationmaster.repository.AddressRepository;
import com.adminapplicationmaster.service.LocationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * API Controller for Location Service
 * Requires JWT authentication
 */
@RestController
@RequestMapping("/api/v1/location_services")
@Slf4j
public class ApiLocationServiceController {

    @Autowired
    private LocationService locationService;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> request) {
        try {
            Object addressParam = request.get("address");
            ResponseEntity<String> response = locationService.getAddressInfo(addressParam);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                
                if ("address_eligible".equals(responseBody.get("message").asText())) {
                    JsonNode formattedAddress = responseBody.get("formatted_address");
                    
                    Address newAddress = Address.builder()
                            .street(formattedAddress.get("street").asText())
                            .city(formattedAddress.get("city").asText())
                            .zip(formattedAddress.get("zip").asText())
                            .state(formattedAddress.get("state").asText())
                            .county(formattedAddress.get("county").asText())
                            .build();
                    
                    Address saved = addressRepository.save(newAddress);
                    return ResponseEntity.ok(saved);
                } else {
                    return ResponseEntity.status(404)
                            .body(Map.of("message", "Address not eligible."));
                }
            } else if (response.getStatusCode().value() == 404) {
                return ResponseEntity.status(404)
                        .body(Map.of("message", "Address not eligible."));
            } else {
                return ResponseEntity.status(500)
                        .body(Map.of("message", "Location service error"));
            }
        } catch (Exception e) {
            log.error("Error in location service", e);
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Location service error"));
        }
    }
}