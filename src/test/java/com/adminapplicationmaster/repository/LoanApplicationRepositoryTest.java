package com.adminapplicationmaster.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import com.adminapplicationmaster.config.EncryptionConverter;
import com.adminapplicationmaster.domain.entity.Address;
import com.adminapplicationmaster.domain.entity.LoanApplication;
import com.adminapplicationmaster.util.EncryptionUtil;

@DataJpaTest
@ActiveProfiles("test")
@Import({EncryptionConverter.class}) 
class LoanApplicationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private AddressRepository addressRepository;

    @MockBean
    private EncryptionUtil encryptionUtil;

    private Address testAddress;
    private LoanApplication pendingApplication;
    private LoanApplication approvedApplication;
    private LoanApplication rejectedApplication;

    @BeforeEach
    void setUp() {
        // Mock encryption behavior - passthrough for tests
        when(encryptionUtil.encrypt(anyString()))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(encryptionUtil.decrypt(anyString()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Create test address
        testAddress = Address.builder()
                .street("123 Main St")
                .unitNumber("Apt 4")
                .city("Springfield")
                .state("IL")
                .zip("62701")
                .county("Sangamon")
                .build();
        testAddress = addressRepository.save(testAddress);

        // Create pending application
        pendingApplication = LoanApplication.builder()
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
                .address(testAddress)
                .build();

        // Create approved application
        approvedApplication = LoanApplication.builder()
                .firstName("Jane")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1985, 5, 15))
                .ssn("987-65-4321")
                .email("jane.smith@example.com")
                .phone("555-5678")
                .income(85000.0)
                .incomeType("hourly")
                .requestedLoanAmount(30000.0)
                .status("approved")
                .address(testAddress)
                .build();

        // Create rejected application
        rejectedApplication = LoanApplication.builder()
                .firstName("Bob")
                .lastName("Johnson")
                .dateOfBirth(LocalDate.of(1995, 3, 20))
                .ssn("555-55-5555")
                .email("bob.johnson@example.com")
                .phone("555-9999")
                .income(45000.0)
                .incomeType("salary")
                .requestedLoanAmount(50000.0)
                .status("rejected")
                .address(testAddress)
                .build();

        loanApplicationRepository.save(pendingApplication);
        loanApplicationRepository.save(approvedApplication);
        loanApplicationRepository.save(rejectedApplication);
        entityManager.flush();
    }

    @Test
    void findAll_shouldReturnAllApplications() {
        List<LoanApplication> applications = loanApplicationRepository.findAll();

        assertThat(applications).hasSize(3);
        assertThat(applications).extracting(LoanApplication::getStatus)
                .containsExactlyInAnyOrder("pending", "approved", "rejected");
    }

    @Test
    void findById_shouldReturnApplication() {
        Optional<LoanApplication> found = loanApplicationRepository.findById(pendingApplication.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getLastName()).isEqualTo("Doe");
        assertThat(found.get().getStatus()).isEqualTo("pending");
    }

    @Test
    void findById_shouldReturnEmptyForNonExistent() {
        Optional<LoanApplication> found = loanApplicationRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void save_shouldPersistNewApplication() {
        LoanApplication newApplication = LoanApplication.builder()
                .firstName("Alice")
                .lastName("Williams")
                .dateOfBirth(LocalDate.of(1992, 7, 10))
                .ssn("111-22-3333")
                .email("alice@example.com")
                .phone("555-0000")
                .income(65000.0)
                .incomeType("salary")
                .requestedLoanAmount(20000.0)
                .status("pending")
                .address(testAddress)
                .build();

        LoanApplication saved = loanApplicationRepository.save(newApplication);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Alice");
        
        Optional<LoanApplication> found = loanApplicationRepository.findById(saved.getId());
        assertThat(found).isPresent();
        
        // Verify encryption was called for SSN
        verify(encryptionUtil, atLeastOnce()).encrypt(anyString());
    }

    @Test
    void save_shouldUpdateExistingApplication() {
        pendingApplication.setStatus("approved");
        pendingApplication.setIncome(80000.0);

        LoanApplication updated = loanApplicationRepository.save(pendingApplication);

        assertThat(updated.getStatus()).isEqualTo("approved");
        assertThat(updated.getIncome()).isEqualTo(80000.0);
        
        Optional<LoanApplication> found = loanApplicationRepository.findById(pendingApplication.getId());
        assertThat(found.get().getStatus()).isEqualTo("approved");
    }

    @Test
    void delete_shouldRemoveApplication() {
        Long id = pendingApplication.getId();
        
        loanApplicationRepository.delete(pendingApplication);
        entityManager.flush();

        Optional<LoanApplication> found = loanApplicationRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void findByStatusIn_shouldReturnFilteredApplications() {
        List<String> statuses = Arrays.asList("approved", "rejected");
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<LoanApplication> page = loanApplicationRepository.findByStatusIn(statuses, pageRequest);

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent()).extracting(LoanApplication::getStatus)
                .containsExactlyInAnyOrder("approved", "rejected");
        assertThat(page.getContent()).extracting(LoanApplication::getFirstName)
                .containsExactlyInAnyOrder("Jane", "Bob");
    }

    @Test
    void findByStatusIn_shouldReturnOnlyPendingApplications() {
        List<String> statuses = Arrays.asList("pending");
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<LoanApplication> page = loanApplicationRepository.findByStatusIn(statuses, pageRequest);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getStatus()).isEqualTo("pending");
        assertThat(page.getContent().get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    void findByStatusIn_shouldReturnEmptyPageForNonMatchingStatus() {
        List<String> statuses = Arrays.asList("withdrawn");
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<LoanApplication> page = loanApplicationRepository.findByStatusIn(statuses, pageRequest);

        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isEqualTo(0);
    }

    @Test
    void findByStatusIn_shouldReturnAllApplicationsForAllStatuses() {
        List<String> statuses = Arrays.asList("pending", "approved", "rejected");
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<LoanApplication> page = loanApplicationRepository.findByStatusIn(statuses, pageRequest);

        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    void findByStatusIn_shouldSupportPagination() {
        // Create additional applications
        for (int i = 0; i < 5; i++) {
            LoanApplication app = LoanApplication.builder()
                    .firstName("Test" + i)
                    .lastName("User" + i)
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .ssn("000-00-000" + i)
                    .email("test" + i + "@example.com")
                    .phone("555-000" + i)
                    .income(50000.0)
                    .incomeType("salary")
                    .requestedLoanAmount(15000.0)
                    .status("pending")
                    .address(testAddress)
                    .build();
            loanApplicationRepository.save(app);
        }
        entityManager.flush();

        List<String> statuses = Arrays.asList("pending");
        PageRequest firstPage = PageRequest.of(0, 3);
        PageRequest secondPage = PageRequest.of(1, 3);

        Page<LoanApplication> page1 = loanApplicationRepository.findByStatusIn(statuses, firstPage);
        Page<LoanApplication> page2 = loanApplicationRepository.findByStatusIn(statuses, secondPage);

        assertThat(page1.getContent()).hasSize(3);
        assertThat(page2.getContent()).hasSize(3);
        assertThat(page1.getTotalElements()).isEqualTo(6); // 1 original + 5 new
        assertThat(page1.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByStatusIn_shouldRespectSortOrder() {
        List<String> statuses = Arrays.asList("pending", "approved", "rejected");
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "firstName"));

        Page<LoanApplication> page = loanApplicationRepository.findByStatusIn(statuses, pageRequest);

        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getContent().get(0).getFirstName()).isEqualTo("Bob");
        assertThat(page.getContent().get(1).getFirstName()).isEqualTo("Jane");
        assertThat(page.getContent().get(2).getFirstName()).isEqualTo("John");
    }

    @Test
    void cascadeOperations_shouldHandleAddressRelationship() {
        LoanApplication app = loanApplicationRepository.findById(pendingApplication.getId()).get();
        
        assertThat(app.getAddress()).isNotNull();
        assertThat(app.getAddress().getStreet()).isEqualTo("123 Main St");
        assertThat(app.getAddress().getCity()).isEqualTo("Springfield");
    }

    @Test
    void encryption_shouldBeCalledWhenSavingSSN() {
        LoanApplication app = LoanApplication.builder()
                .firstName("Test")
                .lastName("User")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .ssn("999-99-9999")
                .email("test@example.com")
                .phone("555-0000")
                .income(50000.0)
                .incomeType("salary")
                .requestedLoanAmount(10000.0)
                .status("pending")
                .address(testAddress)
                .build();

        loanApplicationRepository.save(app);
        entityManager.flush();

        // Verify encrypt was called (for SSN field)
        verify(encryptionUtil, atLeastOnce()).encrypt("999-99-9999");
    }

    @Test
    void decryption_shouldBeCalledWhenReadingSSN() {
        // Clear the persistence context to force a database read
        entityManager.clear();
        LoanApplication app = loanApplicationRepository.findById(pendingApplication.getId()).get();
        
        String ssn = app.getSsn();
        
        assertThat(ssn).isEqualTo("123-45-6789");
        // Verify decrypt was called when reading from database
        verify(encryptionUtil, atLeastOnce()).decrypt(anyString());
    }
}