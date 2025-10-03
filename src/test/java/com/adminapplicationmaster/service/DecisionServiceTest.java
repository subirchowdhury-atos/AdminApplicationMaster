package com.adminapplicationmaster.service;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
class DecisionServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DecisionService decisionService;

    @Captor
    private ArgumentCaptor<HttpEntity<String>> requestCaptor;

    private String testHost = "http://decision-service.com";
    private String testApiToken = "test-api-token-123";
    private Map<String, Object> testApplication;

    @BeforeEach
    void setUp() {
        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(decisionService, "host", testHost);
        ReflectionTestUtils.setField(decisionService, "apiToken", testApiToken);

        // Create test application data
        testApplication = new HashMap<>();
        testApplication.put("application_id", 1L);
        testApplication.put("first_name", "John");
        testApplication.put("last_name", "Doe");
        testApplication.put("income", 75000);
    }

    @Test
    void getDecision_shouldReturnSuccessfulResponse() throws Exception {
        String requestBody = "{\"application_id\":1}";
        String responseBody = "{\"final_decision\":\"approved\",\"reason\":\"Good credit\"}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok(responseBody);

        when(objectMapper.writeValueAsString(testApplication)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<String> response = decisionService.getDecision(testApplication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseBody, response.getBody());
        verify(objectMapper).writeValueAsString(testApplication);
        verify(restTemplate).postForEntity(
                eq(testHost + "/api/v1/decisions"),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void getDecision_shouldSetCorrectHeaders() throws Exception {
        String requestBody = "{\"application_id\":1}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("{}");

        when(objectMapper.writeValueAsString(testApplication)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), requestCaptor.capture(), eq(String.class)))
                .thenReturn(expectedResponse);

        decisionService.getDecision(testApplication);

        HttpEntity<String> capturedRequest = requestCaptor.getValue();
        HttpHeaders headers = capturedRequest.getHeaders();

        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(testApiToken, headers.getFirst("Api-Token"));
        assertEquals(requestBody, capturedRequest.getBody());
    }

    @Test
    void getDecision_shouldUseCorrectUrl() throws Exception {
        String requestBody = "{\"application_id\":1}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("{}");

        when(objectMapper.writeValueAsString(testApplication)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        decisionService.getDecision(testApplication);

        verify(restTemplate).postForEntity(
                eq(testHost + "/api/v1/decisions"),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void getDecision_shouldHandleRestClientException() throws Exception {
        String requestBody = "{\"application_id\":1}";

        when(objectMapper.writeValueAsString(testApplication)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Connection timeout"));

        ResponseEntity<String> response = decisionService.getDecision(testApplication);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Decision service error", response.getBody());
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void getDecision_shouldHandleJsonProcessingException() throws Exception {
        when(objectMapper.writeValueAsString(testApplication))
                .thenThrow(new RuntimeException("JSON serialization error"));

        ResponseEntity<String> response = decisionService.getDecision(testApplication);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Decision service error", response.getBody());
        verify(objectMapper).writeValueAsString(testApplication);
        verify(restTemplate, never()).postForEntity(anyString(), any(), any());
    }

    @Test
    void getDecision_shouldHandleNullResponse() throws Exception {
        String requestBody = "{\"application_id\":1}";

        when(objectMapper.writeValueAsString(testApplication)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(null);

        ResponseEntity<String> response = decisionService.getDecision(testApplication);

        assertNull(response);
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void getDecision_shouldHandle4xxResponse() throws Exception {
        String requestBody = "{\"application_id\":1}";
        String errorBody = "{\"error\":\"Invalid request\"}";
        ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorBody);

        when(objectMapper.writeValueAsString(testApplication)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(errorResponse);

        ResponseEntity<String> response = decisionService.getDecision(testApplication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorBody, response.getBody());
    }

    @Test
    void getDecision_shouldHandle5xxResponse() throws Exception {
        String requestBody = "{\"application_id\":1}";
        String errorBody = "{\"error\":\"Service unavailable\"}";
        ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorBody);

        when(objectMapper.writeValueAsString(testApplication)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(errorResponse);

        ResponseEntity<String> response = decisionService.getDecision(testApplication);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(errorBody, response.getBody());
    }

    @Test
    void getDecision_shouldHandleEmptyApplicationObject() throws Exception {
        Map<String, Object> emptyApplication = new HashMap<>();
        String requestBody = "{}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("{\"status\":\"ok\"}");

        when(objectMapper.writeValueAsString(emptyApplication)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<String> response = decisionService.getDecision(emptyApplication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(objectMapper).writeValueAsString(emptyApplication);
    }

    @Test
    void getDecision_shouldHandleComplexApplicationObject() throws Exception {
        Map<String, Object> complexApplication = new HashMap<>();
        complexApplication.put("application_id", 1L);
        complexApplication.put("applicant", Map.of(
                "name", "John Doe",
                "address", Map.of(
                        "street", "123 Main St",
                        "city", "Springfield"
                )
        ));
        complexApplication.put("loan_details", Map.of(
                "amount", 50000,
                "term", 60
        ));

        String requestBody = "{\"complex\":\"json\"}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("{\"decision\":\"approved\"}");

        when(objectMapper.writeValueAsString(complexApplication)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<String> response = decisionService.getDecision(complexApplication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(objectMapper).writeValueAsString(complexApplication);
    }

    @Test
    void getDecision_shouldHandleNullApplication() throws Exception {
        when(objectMapper.writeValueAsString(null))
                .thenThrow(new IllegalArgumentException("Cannot serialize null"));

        ResponseEntity<String> response = decisionService.getDecision(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Decision service error", response.getBody());
    }

    @Test
    void getDecision_shouldSerializeApplicationBeforeCallingRestTemplate() throws Exception {
        String requestBody = "{\"application_id\":1}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("{}");

        when(objectMapper.writeValueAsString(testApplication)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        decisionService.getDecision(testApplication);

        // Verify order of operations
        verify(objectMapper).writeValueAsString(testApplication);
        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void getDecision_shouldNotModifyOriginalApplication() throws Exception {
        String originalJson = testApplication.toString();
        String requestBody = "{\"application_id\":1}";
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("{}");

        when(objectMapper.writeValueAsString(testApplication)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(expectedResponse);

        decisionService.getDecision(testApplication);

        // Verify application object wasn't modified
        assertEquals(originalJson, testApplication.toString());
    }

    @Test
    void getDecision_shouldHandleNetworkTimeout() throws Exception {
        String requestBody = "{\"application_id\":1}";

        when(objectMapper.writeValueAsString(testApplication)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Read timed out"));

        ResponseEntity<String> response = decisionService.getDecision(testApplication);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Decision service error", response.getBody());
    }

    @Test
    void getDecision_shouldHandleConnectionRefused() throws Exception {
        String requestBody = "{\"application_id\":1}";

        when(objectMapper.writeValueAsString(testApplication)).thenReturn(requestBody);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Connection refused"));

        ResponseEntity<String> response = decisionService.getDecision(testApplication);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Decision service error", response.getBody());
    }
}