package com.adminapplicationmaster.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.adminapplicationmaster.domain.entity.Address;
import com.adminapplicationmaster.domain.entity.ApplicationDecision;
import com.adminapplicationmaster.domain.entity.LoanApplication;
import com.adminapplicationmaster.repository.ApplicationDecisionRepository;
import com.adminapplicationmaster.repository.LoanApplicationRepository;
import com.adminapplicationmaster.service.DecisionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ApiApplicationServiceControllerTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private ApplicationDecisionRepository applicationDecisionRepository;

    @Mock
    private DecisionService decisionService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ApiApplicationServiceController controller;

    private LoanApplication testApplication;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        testAddress = Address.builder()
                .id(1L)
                .street("123 Main St")
                .unitNumber("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .county("Sangamon")
                .build();

        testApplication = LoanApplication.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .ssn("123-45-6789")
                .email("john.doe@example.com")
                .phone("555-1234")
                .income(Double.valueOf("75000"))
                .incomeType("salary")
                .requestedLoanAmount(Double.valueOf("25000"))
                .address(testAddress)
                .build();
    }

    @Test
    void index_shouldReturnAllLoanApplications() {
        // Create a Page object with test data
        Page<LoanApplication> page = new PageImpl<>(Arrays.asList(testApplication));
        
        when(loanApplicationRepository.findAll(any(Pageable.class))).thenReturn(page);

        ResponseEntity<?> response = controller.index(null, 0, 20);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        // The response body is now a Page object
        Page<LoanApplication> resultPage = (Page<LoanApplication>) response.getBody();
        assertEquals(1, resultPage.getContent().size());
        assertEquals(testApplication, resultPage.getContent().get(0));
        
        verify(loanApplicationRepository).findAll(any(Pageable.class));
    }

    @Test
    void index_shouldFilterByStatus() {
        Page<LoanApplication> page = new PageImpl<>(Arrays.asList(testApplication));
        
        when(loanApplicationRepository.findByStatus(eq("pending"), any(Pageable.class)))
            .thenReturn(page);

        ResponseEntity<?> response = controller.index("pending", 0, 20);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Page<LoanApplication> resultPage = (Page<LoanApplication>) response.getBody();
        assertEquals(1, resultPage.getContent().size());
        
        verify(loanApplicationRepository).findByStatus(eq("pending"), any(Pageable.class));
        verify(loanApplicationRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void index_shouldHandleEmptyStatus() {
        Page<LoanApplication> page = new PageImpl<>(Arrays.asList(testApplication));
        
        when(loanApplicationRepository.findAll(any(Pageable.class))).thenReturn(page);

        ResponseEntity<?> response = controller.index("", 0, 20);

        assertEquals(200, response.getStatusCodeValue());
        verify(loanApplicationRepository).findAll(any(Pageable.class));
        verify(loanApplicationRepository, never()).findByStatus(anyString(), any(Pageable.class));
    }

    @Test
    void index_shouldHandlePaginationParameters() {
        Page<LoanApplication> page = new PageImpl<>(Arrays.asList(testApplication));
        
        when(loanApplicationRepository.findAll(any(Pageable.class))).thenReturn(page);

        ResponseEntity<?> response = controller.index(null, 2, 50);

        assertEquals(200, response.getStatusCodeValue());
        
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(loanApplicationRepository).findAll(pageableCaptor.capture());
        
        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(2, capturedPageable.getPageNumber());
        assertEquals(50, capturedPageable.getPageSize());
    }

    @Test
    void create_shouldSaveLoanApplication() {
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(testApplication);

        ResponseEntity<?> response = controller.create(testApplication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testApplication, response.getBody());
        verify(loanApplicationRepository).save(testApplication);
    }

    @Test
    void create_shouldReturnErrorOnException() {
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = controller.create(testApplication);

        assertEquals(422, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        verify(loanApplicationRepository).save(testApplication);
    }

    @Test
    void show_shouldReturnLoanApplicationWhenFound() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        ResponseEntity<?> response = controller.show(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testApplication, response.getBody());
        verify(loanApplicationRepository).findById(1L);
    }

    @Test
    void show_shouldReturn400WhenNotFound() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.show(1L);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        verify(loanApplicationRepository).findById(1L);
    }

    @Test
    void update_shouldUpdateExistingLoanApplication() {
        LoanApplication updatedData = LoanApplication.builder()
                .firstName("Jane")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1985, 5, 15))
                .ssn("987-65-4321")
                .email("jane.smith@example.com")
                .phone("555-5678")
                .income(Double.valueOf("85000"))
                .incomeType("hourly")
                .requestedLoanAmount(Double.valueOf("30000"))
                .build();

        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(testApplication);

        ResponseEntity<?> response = controller.update(1L, updatedData);

        assertEquals(200, response.getStatusCodeValue());
        verify(loanApplicationRepository).findById(1L);
        verify(loanApplicationRepository).save(testApplication);
        
        assertEquals("Jane", testApplication.getFirstName());
        assertEquals("Smith", testApplication.getLastName());
        assertEquals("jane.smith@example.com", testApplication.getEmail());
    }

    @Test
    void update_shouldReturn422WhenNotFound() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.update(1L, testApplication);

        assertEquals(422, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        verify(loanApplicationRepository).findById(1L);
        verify(loanApplicationRepository, never()).save(any());
    }

    @Test
    void decisionCheck_shouldCreateApplicationDecisionOnSuccess() throws Exception {
        String decisionResponse = "{\"final_decision\":\"approved\",\"reason\":\"Good credit\"}";
        
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(decisionService.getDecision(anyMap())).thenReturn(ResponseEntity.ok(decisionResponse));
        when(objectMapper.readTree(decisionResponse)).thenReturn(
                new ObjectMapper().readTree(decisionResponse)
        );
        when(objectMapper.writeValueAsString(anyMap())).thenReturn("{}");
        
        ApplicationDecision savedDecision = ApplicationDecision.builder()
                .id(1L)
                .loanApplication(testApplication)
                .decision("approved")
                .build();
        
        when(applicationDecisionRepository.save(any(ApplicationDecision.class)))
                .thenReturn(savedDecision);

        ResponseEntity<?> response = controller.decisionCheck(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(loanApplicationRepository).findById(1L);
        verify(decisionService).getDecision(anyMap());
        verify(applicationDecisionRepository).save(any(ApplicationDecision.class));
    }

    @Test
    void decisionCheck_shouldReturn400OnDecisionServiceError() {
        when(loanApplicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(decisionService.getDecision(anyMap())).thenReturn(ResponseEntity.status(500).body("Error"));

        ResponseEntity<?> response = controller.decisionCheck(1L);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        verify(applicationDecisionRepository, never()).save(any());
    }

    @Test
    void decisionCheck_shouldReturn500OnException() {
        when(loanApplicationRepository.findById(1L))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = controller.decisionCheck(1L);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
    }
}