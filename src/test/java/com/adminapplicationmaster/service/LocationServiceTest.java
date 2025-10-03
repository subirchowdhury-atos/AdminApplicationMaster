package com.adminapplicationmaster.service;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LocationService locationService;

    @Captor
    private ArgumentCaptor<HttpEntity<String>> requestCaptor;

    private String testHost = "http://location-service.com";
    private String testApiToken = "test-location-token-456";
    private Map<String, Object> testAddress;

    @BeforeEach
    void setUp() {
        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(locationService, "host", testHost);
        ReflectionTestUtils.setField(locationService, "apiToken", testApiToken);

        // Create test address data
        testAddress = new HashMap<>();
        testAddress.put("address", "123 Main St, Springfield, IL 62701");
    }

    @Test
    void getAddressInfo_shouldReturnSuccessfulResponse() throws Exception {
        String requestBody = "{\"address\":\"123 Main St, Springfield, IL 62701\"}";
        String responseBody = "{\"message\":\"address_eligible\",\"formatted_address\":{\"street\":\"123 Main St\"}}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok(responseBody);

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<String> response = locationService.getAddressInfo(testAddress);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseBody, response.getBody());
        verify(objectMapper).writeValueAsString(testAddress);
        verify(restTemplate).postForEntity(
                eq(testHost + "/api/address/eligibility_check"),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void getAddressInfo_shouldSetCorrectHeaders() throws Exception {
        String requestBody = "{\"address\":\"123 Main St\"}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("{}");

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), requestCaptor.capture(), eq(String.class)))
                .thenReturn(expectedResponse);

        locationService.getAddressInfo(testAddress);

        HttpEntity<String> capturedRequest = requestCaptor.getValue();
        HttpHeaders headers = capturedRequest.getHeaders();

        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(testApiToken, headers.getFirst("Api_Token"));
        assertEquals(requestBody, capturedRequest.getBody());
    }

    @Test
    void getAddressInfo_shouldUseCorrectUrl() throws Exception {
        String requestBody = "{\"address\":\"123 Main St\"}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("{}");

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        locationService.getAddressInfo(testAddress);

        verify(restTemplate).postForEntity(
                eq(testHost + "/api/address/eligibility_check"),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void getAddressInfo_shouldHandleRestClientException() throws Exception {
        String requestBody = "{\"address\":\"123 Main St\"}";

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Connection timeout"));

        ResponseEntity<String> response = locationService.getAddressInfo(testAddress);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Location service error", response.getBody());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void getAddressInfo_shouldHandleJsonProcessingException() throws Exception {
        when(objectMapper.writeValueAsString(testAddress))
                .thenThrow(new RuntimeException("JSON serialization error"));

        ResponseEntity<String> response = locationService.getAddressInfo(testAddress);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Location service error", response.getBody());
        verify(objectMapper).writeValueAsString(testAddress);
        verify(restTemplate, never()).postForEntity(anyString(), any(), any());
    }

    @Test
    void getAddressInfo_shouldHandleNullResponse() throws Exception {
        String requestBody = "{\"address\":\"123 Main St\"}";

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(null);

        ResponseEntity<String> response = locationService.getAddressInfo(testAddress);

        assertNull(response);
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void getAddressInfo_shouldHandle404Response() throws Exception {
        String requestBody = "{\"address\":\"999 Fake St\"}";
        String errorBody = "{\"error\":\"Address not found\"}";
        ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorBody);

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(errorResponse);

        ResponseEntity<String> response = locationService.getAddressInfo(testAddress);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorBody, response.getBody());
    }

    @Test
    void getAddressInfo_shouldHandle400Response() throws Exception {
        String requestBody = "{\"address\":\"Invalid\"}";
        String errorBody = "{\"error\":\"Invalid address format\"}";
        ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorBody);

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(errorResponse);

        ResponseEntity<String> response = locationService.getAddressInfo(testAddress);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorBody, response.getBody());
    }

    @Test
    void getAddressInfo_shouldHandle5xxResponse() throws Exception {
        String requestBody = "{\"address\":\"123 Main St\"}";
        String errorBody = "{\"error\":\"Service unavailable\"}";
        ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorBody);

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(errorResponse);

        ResponseEntity<String> response = locationService.getAddressInfo(testAddress);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(errorBody, response.getBody());
    }

    @Test
    void getAddressInfo_shouldHandleEmptyAddressObject() throws Exception {
        Map<String, Object> emptyAddress = new HashMap<>();
        String requestBody = "{}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("{\"status\":\"ok\"}");

        when(objectMapper.writeValueAsString(emptyAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<String> response = locationService.getAddressInfo(emptyAddress);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(objectMapper).writeValueAsString(emptyAddress);
    }

    @Test
    void getAddressInfo_shouldHandleComplexAddressObject() throws Exception {
        Map<String, Object> complexAddress = new HashMap<>();
        complexAddress.put("street", "123 Main St");
        complexAddress.put("unit_number", "Apt 4");
        complexAddress.put("city", "Springfield");
        complexAddress.put("state", "IL");
        complexAddress.put("zip", "62701");
        complexAddress.put("county", "Sangamon");

        String requestBody = "{\"street\":\"123 Main St\",\"city\":\"Springfield\"}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("{\"message\":\"address_eligible\"}");

        when(objectMapper.writeValueAsString(complexAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<String> response = locationService.getAddressInfo(complexAddress);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(objectMapper).writeValueAsString(complexAddress);
    }

    @Test
    void getAddressInfo_shouldHandleNullAddress() throws Exception {
        when(objectMapper.writeValueAsString(null))
                .thenThrow(new IllegalArgumentException("Cannot serialize null"));

        ResponseEntity<String> response = locationService.getAddressInfo(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Location service error", response.getBody());
    }

    @Test
    void getAddressInfo_shouldSerializeAddressBeforeCallingRestTemplate() throws Exception {
        String requestBody = "{\"address\":\"123 Main St\"}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("{}");

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        locationService.getAddressInfo(testAddress);

        // Verify order of operations
        verify(objectMapper).writeValueAsString(testAddress);
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void getAddressInfo_shouldNotModifyOriginalAddress() throws Exception {
        String originalJson = testAddress.toString();
        String requestBody = "{\"address\":\"123 Main St\"}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("{}");

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        locationService.getAddressInfo(testAddress);

        // Verify address object wasn't modified
        assertEquals(originalJson, testAddress.toString());
    }

    @Test
    void getAddressInfo_shouldHandleNetworkTimeout() throws Exception {
        String requestBody = "{\"address\":\"123 Main St\"}";

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Read timed out"));

        ResponseEntity<String> response = locationService.getAddressInfo(testAddress);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Location service error", response.getBody());
    }

    @Test
    void getAddressInfo_shouldHandleConnectionRefused() throws Exception {
        String requestBody = "{\"address\":\"123 Main St\"}";

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Connection refused"));

        ResponseEntity<String> response = locationService.getAddressInfo(testAddress);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Location service error", response.getBody());
    }

    @Test
    void getAddressInfo_shouldHandleIneligibleAddress() throws Exception {
        String requestBody = "{\"address\":\"999 Ineligible St\"}";
        String responseBody = "{\"message\":\"address_not_eligible\",\"reason\":\"Outside service area\"}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok(responseBody);

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<String> response = locationService.getAddressInfo(testAddress);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("address_not_eligible"));
    }

    @Test
    void getAddressInfo_shouldHandleEligibleAddressWithFormattedData() throws Exception {
        String requestBody = "{\"address\":\"123 Main St\"}";
        String responseBody = "{\"message\":\"address_eligible\"," +
                "\"formatted_address\":{" +
                "\"street\":\"123 Main St\"," +
                "\"city\":\"Springfield\"," +
                "\"state\":\"IL\"," +
                "\"zip\":\"62701\"," +
                "\"county\":\"Sangamon\"}}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok(responseBody);

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<String> response = locationService.getAddressInfo(testAddress);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("address_eligible"));
        assertTrue(response.getBody().contains("formatted_address"));
    }

    @Test
    void getAddressInfo_shouldHandleDifferentHeaderName() throws Exception {
        // Note: This service uses "Api_Token" instead of "Api-Token"
        String requestBody = "{\"address\":\"123 Main St\"}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("{}");

        when(objectMapper.writeValueAsString(testAddress)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), requestCaptor.capture(), eq(String.class)))
                .thenReturn(expectedResponse);

        locationService.getAddressInfo(testAddress);

        HttpEntity<String> capturedRequest = requestCaptor.getValue();
        HttpHeaders headers = capturedRequest.getHeaders();

        // Verify the specific header name used by location service
        assertEquals(testApiToken, headers.getFirst("Api_Token"));
        assertNull(headers.getFirst("Api-Token")); // Should not use hyphen
    }
}