package com.adminapplicationmaster.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.adminapplicationmaster.config.EncryptionConverter;
import com.adminapplicationmaster.domain.entity.Address;
import com.adminapplicationmaster.domain.entity.ApplicationDecision;
import com.adminapplicationmaster.domain.entity.LoanApplication;
import com.adminapplicationmaster.util.EncryptionUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
@Import({EncryptionConverter.class}) 
class ApplicationDecisionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicationDecisionRepository applicationDecisionRepository;

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private AddressRepository addressRepository;

    @MockBean
    private EncryptionUtil encryptionUtil;

    private LoanApplication testLoanApplication;
    private ApplicationDecision testDecision1;
    private ApplicationDecision testDecision2;

    @BeforeEach
    void setUp() {
        // Mock encryption behavior - passthrough for tests
        when(encryptionUtil.encrypt(anyString()))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(encryptionUtil.decrypt(anyString()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Create address
        Address address = Address.builder()
                .street("123 Main St")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .county("Sangamon")
                .build();
        address = addressRepository.save(address);

        // Create loan application
        testLoanApplication = LoanApplication.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .ssn("123-45-6789")
                .email("john.doe@example.com")
                .phone("555-1234")
                .income(75000.0)
                .incomeType("salary")
                .requestedLoanAmount(25000.0)
                .status("pending")
                .address(address)
                .build();
        testLoanApplication = loanApplicationRepository.save(testLoanApplication);

        // Create first decision
        testDecision1 = ApplicationDecision.builder()
                .loanApplication(testLoanApplication)
                .request("{\"application_id\":1,\"income\":75000}")
                .response("{\"final_decision\":\"approved\",\"reason\":\"Good credit\"}")
                .decision("approved")
                .build();

        // Create second decision
        testDecision2 = ApplicationDecision.builder()
                .loanApplication(testLoanApplication)
                .request("{\"application_id\":1,\"income\":75000,\"updated\":true}")
                .response("{\"final_decision\":\"approved\",\"reason\":\"Excellent payment history\"}")
                .decision("approved")
                .build();

        applicationDecisionRepository.save(testDecision1);
        applicationDecisionRepository.save(testDecision2);
        entityManager.flush();
    }

    @Test
    void findAll_shouldReturnAllDecisions() {
        List<ApplicationDecision> decisions = applicationDecisionRepository.findAll();

        assertThat(decisions).hasSize(2);
        assertThat(decisions).extracting(ApplicationDecision::getDecision)
                .containsOnly("approved");
    }

    @Test
    void findById_shouldReturnDecision() {
        Optional<ApplicationDecision> found = applicationDecisionRepository.findById(testDecision1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getDecision()).isEqualTo("approved");
        assertThat(found.get().getRequest()).contains("application_id");
        assertThat(found.get().getResponse()).contains("final_decision");
    }

    @Test
    void findById_shouldReturnEmptyForNonExistent() {
        Optional<ApplicationDecision> found = applicationDecisionRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void save_shouldPersistNewDecision() {
        ApplicationDecision newDecision = ApplicationDecision.builder()
                .loanApplication(testLoanApplication)
                .request("{\"application_id\":1,\"new_request\":true}")
                .response("{\"final_decision\":\"rejected\",\"reason\":\"High debt ratio\"}")
                .decision("rejected")
                .build();

        ApplicationDecision saved = applicationDecisionRepository.save(newDecision);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDecision()).isEqualTo("rejected");
        assertThat(saved.getLoanApplication()).isEqualTo(testLoanApplication);
        
        Optional<ApplicationDecision> found = applicationDecisionRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getResponse()).contains("High debt ratio");
    }

    @Test
    void save_shouldUpdateExistingDecision() {
        testDecision1.setDecision("rejected");
        testDecision1.setResponse("{\"final_decision\":\"rejected\",\"reason\":\"Updated decision\"}");

        ApplicationDecision updated = applicationDecisionRepository.save(testDecision1);

        assertThat(updated.getDecision()).isEqualTo("rejected");
        assertThat(updated.getResponse()).contains("Updated decision");
        
        Optional<ApplicationDecision> found = applicationDecisionRepository.findById(testDecision1.getId());
        assertThat(found.get().getDecision()).isEqualTo("rejected");
    }

    @Test
    void delete_shouldRemoveDecision() {
        Long id = testDecision1.getId();
        
        applicationDecisionRepository.delete(testDecision1);
        entityManager.flush();

        Optional<ApplicationDecision> found = applicationDecisionRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_shouldRemoveDecision() {
        Long id = testDecision1.getId();
        
        applicationDecisionRepository.deleteById(id);
        entityManager.flush();

        Optional<ApplicationDecision> found = applicationDecisionRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void count_shouldReturnCorrectNumber() {
        long count = applicationDecisionRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void existsById_shouldReturnTrueForExistingDecision() {
        boolean exists = applicationDecisionRepository.existsById(testDecision1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalseForNonExistentDecision() {
        boolean exists = applicationDecisionRepository.existsById(999L);

        assertThat(exists).isFalse();
    }

    @Test
    void save_shouldMaintainRelationshipWithLoanApplication() {
        ApplicationDecision found = applicationDecisionRepository.findById(testDecision1.getId()).get();
        
        assertThat(found.getLoanApplication()).isNotNull();
        assertThat(found.getLoanApplication().getId()).isEqualTo(testLoanApplication.getId());
        assertThat(found.getLoanApplication().getFirstName()).isEqualTo("John");
        assertThat(found.getLoanApplication().getLastName()).isEqualTo("Doe");
    }

    @Test
    void save_shouldHandleMultipleDecisionsForSameApplication() {
        ApplicationDecision decision3 = ApplicationDecision.builder()
                .loanApplication(testLoanApplication)
                .request("{\"application_id\":1,\"third_request\":true}")
                .response("{\"final_decision\":\"approved\",\"reason\":\"Third review\"}")
                .decision("approved")
                .build();

        applicationDecisionRepository.save(decision3);
        entityManager.flush();

        List<ApplicationDecision> allDecisions = applicationDecisionRepository.findAll();
        assertThat(allDecisions).hasSize(3);
        
        long decisionsForApp = allDecisions.stream()
                .filter(d -> d.getLoanApplication().getId().equals(testLoanApplication.getId()))
                .count();
        assertThat(decisionsForApp).isEqualTo(3);
    }

    @Test
    void save_shouldPersistLargeJsonPayloads() {
        String largeRequest = "{\"application_id\":1,\"data\":\"" + "x".repeat(1000) + "\"}";
        String largeResponse = "{\"final_decision\":\"approved\",\"details\":\"" + "y".repeat(1000) + "\"}";
        
        ApplicationDecision largeDecision = ApplicationDecision.builder()
                .loanApplication(testLoanApplication)
                .request(largeRequest)
                .response(largeResponse)
                .decision("approved")
                .build();

        ApplicationDecision saved = applicationDecisionRepository.save(largeDecision);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRequest()).hasSize(largeRequest.length());
        assertThat(saved.getResponse()).hasSize(largeResponse.length());
        
        Optional<ApplicationDecision> found = applicationDecisionRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getRequest()).isEqualTo(largeRequest);
        assertThat(found.get().getResponse()).isEqualTo(largeResponse);
    }

    @Test
    void save_shouldHandleDifferentDecisionTypes() {
        ApplicationDecision approvedDecision = ApplicationDecision.builder()
                .loanApplication(testLoanApplication)
                .request("{\"type\":\"approved\"}")
                .response("{\"final_decision\":\"approved\"}")
                .decision("approved")
                .build();

        ApplicationDecision rejectedDecision = ApplicationDecision.builder()
                .loanApplication(testLoanApplication)
                .request("{\"type\":\"rejected\"}")
                .response("{\"final_decision\":\"rejected\"}")
                .decision("rejected")
                .build();

        ApplicationDecision pendingDecision = ApplicationDecision.builder()
                .loanApplication(testLoanApplication)
                .request("{\"type\":\"pending\"}")
                .response("{\"final_decision\":\"pending\"}")
                .decision("pending")
                .build();

        applicationDecisionRepository.save(approvedDecision);
        applicationDecisionRepository.save(rejectedDecision);
        applicationDecisionRepository.save(pendingDecision);
        entityManager.flush();

        List<ApplicationDecision> allDecisions = applicationDecisionRepository.findAll();
        assertThat(allDecisions).hasSizeGreaterThanOrEqualTo(3);
        
        List<String> decisionTypes = allDecisions.stream()
                .map(ApplicationDecision::getDecision)
                .distinct()
                .toList();
        assertThat(decisionTypes).contains("approved", "rejected", "pending");
    }

    @Test
    void cascadeOperations_shouldNotDeleteLoanApplicationWhenDecisionDeleted() {
        Long appId = testLoanApplication.getId();
        
        applicationDecisionRepository.delete(testDecision1);
        entityManager.flush();

        Optional<LoanApplication> app = loanApplicationRepository.findById(appId);
        assertThat(app).isPresent();
    }

    @Test
    void save_shouldHandleNullableFields() {
        // Test with minimal required fields
        ApplicationDecision minimalDecision = ApplicationDecision.builder()
                .loanApplication(testLoanApplication)
                .decision("approved")
                .build();

        ApplicationDecision saved = applicationDecisionRepository.save(minimalDecision);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDecision()).isEqualTo("approved");
        // request and response might be null depending on entity constraints
    }
}