package com.adminapplicationmaster.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.adminapplicationmaster.domain.entity.User;
import com.adminapplicationmaster.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ApiUsersControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ApiUsersController controller;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role("admin")
                .contact("555-0001")
                .encryptedPassword("$2a$10$encodedPassword")
                .signInCount(0)
                .build();

        testUser2 = User.builder()
                .id(2L)
                .email("test2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .role("user")
                .contact("555-0002")
                .encryptedPassword("$2a$10$encodedPassword2")
                .signInCount(0)
                .build();
    }

    @Test
    void index_shouldReturnAllUsers() {
        List<User> users = Arrays.asList(testUser, testUser2);
        when(userRepository.findAll()).thenReturn(users);

        ResponseEntity<List<User>> response = controller.index();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactlyInAnyOrder(testUser, testUser2);
        verify(userRepository).findAll();
    }

    @Test
    void index_shouldReturnEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        ResponseEntity<List<User>> response = controller.index();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void show_shouldReturnUserWhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = controller.show(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testUser);
        verify(userRepository).findById(1L);
    }

    @Test
    void show_shouldReturn404WhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.show(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("message")).isEqualTo("User not found");
    }

    @Test
    void create_shouldSaveUserWithDefaultPassword() {
        User newUser = User.builder()
                .email("newuser@example.com")
                .firstName("New")
                .lastName("User")
                .role("user")
                .contact("555-0003")
                .signInCount(0)
                .build();

        String encodedPassword = "$2a$10$encodedDefaultPassword";
        
        when(passwordEncoder.encode("12345678")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        ResponseEntity<?> response = controller.create(newUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(newUser);
        verify(passwordEncoder).encode("12345678");
        verify(userRepository).save(newUser);
    }

    @Test
    void create_shouldEncodeProvidedPassword() {
        User newUser = User.builder()
                .email("newuser@example.com")
                .firstName("New")
                .lastName("User")
                .role("user")
                .contact("555-0003")
                .encryptedPassword("mypassword")
                .signInCount(0)
                .build();

        String encodedPassword = "$2a$10$encodedCustomPassword";
        
        when(passwordEncoder.encode("mypassword")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        ResponseEntity<?> response = controller.create(newUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(passwordEncoder).encode("mypassword");
        verify(userRepository).save(newUser);
    }

    @Test
    void create_shouldReturnErrorOnException() {
        User newUser = User.builder()
                .email("newuser@example.com")
                .contact("555-0003")
                .signInCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = controller.create(newUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("errors")).isEqualTo("Database error");
    }

    @Test
    void update_shouldUpdateExistingUser() {
        User updateData = User.builder()
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("Name")
                .role("admin")
                .contact("555-1234")
                .signInCount(0)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = controller.update(1L, updateData);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(testUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(testUser.getFirstName()).isEqualTo("Updated");
        assertThat(testUser.getLastName()).isEqualTo("Name");
        verify(userRepository).save(testUser);
    }

    @Test
    void update_shouldUpdatePasswordIfProvided() {
        User updateData = User.builder()
                .email("test@example.com")
                .contact("555-0001")
                .signInCount(0)
                .encryptedPassword("newpassword")
                .build();

        String encodedPassword = "$2a$10$newEncodedPassword";

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpassword")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = controller.update(1L, updateData);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(passwordEncoder).encode("newpassword");
        assertThat(testUser.getEncryptedPassword()).isEqualTo(encodedPassword);
    }

    @Test
    void update_shouldNotUpdatePasswordIfNotProvided() {
        String originalPassword = testUser.getEncryptedPassword();
        User updateData = User.builder()
                .email("updated@example.com")
                .contact("555-0001")
                .signInCount(0)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = controller.update(1L, updateData);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(passwordEncoder, never()).encode(anyString());
        assertThat(testUser.getEncryptedPassword()).isEqualTo(originalPassword);
    }

    @Test
    void update_shouldReturn404WhenUserNotFound() {
        User updateData = User.builder()
                .email("updated@example.com")
                .contact("555-0001")
                .signInCount(0)
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.update(999L, updateData);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("message")).isEqualTo("User not found");
        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = controller.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("message")).isEqualTo("User deleted successfully");
        verify(userRepository).delete(testUser);
    }

    @Test
    void delete_shouldReturn404WhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.delete(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("message")).isEqualTo("User not found");
        verify(userRepository, never()).delete(any());
    }
}