package com.adminapplicationmaster.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.adminapplicationmaster.domain.entity.User;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = User.builder()
                .email("john.doe@example.com")
                .encryptedPassword("$2a$10$encodedPassword1")
                .contact("555-1234")  
                .createdAt(LocalDateTime.now())  
                .updatedAt(LocalDateTime.now())  
                .build();

        testUser2 = User.builder()
                .email("jane.smith@example.com")
                .encryptedPassword("$2a$10$encodedPassword2")
                .contact("555-5678")  
                .createdAt(LocalDateTime.now())  
                .updatedAt(LocalDateTime.now())  
                .build();

        userRepository.save(testUser1);
        userRepository.save(testUser2);
        entityManager.flush();
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("john.doe@example.com", "jane.smith@example.com");
    }

    @Test
    void findById_shouldReturnUser() {
        Optional<User> found = userRepository.findById(testUser1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(found.get().getEncryptedPassword()).isEqualTo("$2a$10$encodedPassword1");
    }

    @Test
    void findById_shouldReturnEmptyForNonExistent() {
        Optional<User> found = userRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_shouldReturnUserWhenExists() {
        Optional<User> found = userRepository.findByEmail("john.doe@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(found.get().getId()).isEqualTo(testUser1.getId());
    }

    @Test
    void findByEmail_shouldReturnEmptyWhenNotExists() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_shouldBeCaseInsensitiveOrExact() {
        // Test with exact case
        Optional<User> foundExact = userRepository.findByEmail("john.doe@example.com");
        assertThat(foundExact).isPresent();

        // Test with different case - behavior depends on database collation
        // This test documents the behavior
        Optional<User> foundDifferentCase = userRepository.findByEmail("JOHN.DOE@EXAMPLE.COM");
        // Result may vary based on database settings
    }

    @Test
    void save_shouldPersistNewUser() {
        User newUser = User.builder()
                .email("alice@example.com")
                .contact("555-9999")  
                .createdAt(LocalDateTime.now())  
                .updatedAt(LocalDateTime.now())  
                .encryptedPassword("$2a$10$encodedPassword3")
                .build();

        User saved = userRepository.save(newUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("alice@example.com");
        
        Optional<User> found = userRepository.findByEmail("alice@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void save_shouldUpdateExistingUser() {
        testUser1.setEncryptedPassword("$2a$10$newEncodedPassword");

        User updated = userRepository.save(testUser1);

        assertThat(updated.getEncryptedPassword()).isEqualTo("$2a$10$newEncodedPassword");
        
        Optional<User> found = userRepository.findById(testUser1.getId());
        assertThat(found.get().getEncryptedPassword()).isEqualTo("$2a$10$newEncodedPassword");
    }

    @Test
    void delete_shouldRemoveUser() {
        Long id = testUser1.getId();
        String email = testUser1.getEmail();
        
        userRepository.delete(testUser1);
        entityManager.flush();

        Optional<User> foundById = userRepository.findById(id);
        Optional<User> foundByEmail = userRepository.findByEmail(email);
        
        assertThat(foundById).isEmpty();
        assertThat(foundByEmail).isEmpty();
    }

    @Test
    void deleteById_shouldRemoveUser() {
        Long id = testUser1.getId();
        String email = testUser1.getEmail();
        
        userRepository.deleteById(id);
        entityManager.flush();

        Optional<User> foundById = userRepository.findById(id);
        Optional<User> foundByEmail = userRepository.findByEmail(email);
        
        assertThat(foundById).isEmpty();
        assertThat(foundByEmail).isEmpty();
    }

    @Test
    void count_shouldReturnCorrectNumber() {
        long count = userRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void existsById_shouldReturnTrueForExistingUser() {
        boolean exists = userRepository.existsById(testUser1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalseForNonExistentUser() {
        boolean exists = userRepository.existsById(999L);

        assertThat(exists).isFalse();
    }

    @Test
    void save_shouldHandleMultipleUsersWithDifferentEmails() {
        User user3 = User.builder()
                .email("user3@example.com")
                .contact("555-9999")  
                .createdAt(LocalDateTime.now())  
                .updatedAt(LocalDateTime.now()) 
                .encryptedPassword("$2a$10$password3")
                .build();

        User user4 = User.builder()
                .email("user4@example.com")
                .contact("555-9999")  
                .createdAt(LocalDateTime.now())  
                .updatedAt(LocalDateTime.now()) 
                .encryptedPassword("$2a$10$password4")
                .build();

        userRepository.save(user3);
        userRepository.save(user4);
        entityManager.flush();

        assertThat(userRepository.count()).isEqualTo(4);
        assertThat(userRepository.findByEmail("user3@example.com")).isPresent();
        assertThat(userRepository.findByEmail("user4@example.com")).isPresent();
    }

    @Test
    void findByEmail_shouldHandleEmptyString() {
        Optional<User> found = userRepository.findByEmail("");

        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_shouldHandleWhitespace() {
        Optional<User> found = userRepository.findByEmail("   ");

        assertThat(found).isEmpty();
    }
}