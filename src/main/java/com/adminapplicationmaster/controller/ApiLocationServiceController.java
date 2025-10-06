package com.adminapplicationmaster.controller;

import java.util.Map;

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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * API Controller for Location Service
 * Requires JWT authentication
 */
@RestController
@RequestMapping("/api/v1/location_services")
@AllArgsConstructor
@Slf4j
public class ApiLocationServiceController {

    private final LocationService locationService;
    private final AddressRepository addressRepository;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> request) {
        try {
            Object addressParam = request.get("address");
            
            if (addressParam == null) {
                return ResponseEntity.status(400)
                        .body(Map.of("message", "Address parameter is required"));
            }
            
            ResponseEntity<String> response = locationService.getAddressInfo(addressParam);
            
            // Handle successful response from location service
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                String message = responseBody.get("message").asText();
                
                // Address is eligible - save and return
                if ("address_eligible".equals(message)) {
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
                }
                // Address not found or not eligible - return 404 with the message
                else if ("Address Not found".equals(message) || 
                         "address not eligible".equals(message) ||
                         "address missing".equals(message)) {
                    return ResponseEntity.status(404)
                            .body(Map.of("message", message));
                }
                // Unknown response from location service
                else {
                    log.warn("Unexpected message from location service: {}", message);
                    return ResponseEntity.status(404)
                            .body(Map.of("message", "Address not found"));
                }
            }
            // Location service returned error status
            else if (response.getStatusCode().value() == 404) {
                return ResponseEntity.status(404)
                        .body(Map.of("message", "Address not found"));
            }
            // Other error from location service
            else {
                log.error("Location service returned status: {}", response.getStatusCode());
                return ResponseEntity.status(500)
                        .body(Map.of("message", "Location service error"));
            }
        } catch (Exception e) {
            log.error("Error in location service call", e);
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error processing address: " + e.getMessage()));
        }
    }
}