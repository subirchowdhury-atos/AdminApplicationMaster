package com.adminapplicationmaster.controller;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.adminapplicationmaster.domain.entity.User;
import com.adminapplicationmaster.dto.LoginRequest;
import com.adminapplicationmaster.repository.UserRepository;
import com.adminapplicationmaster.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController controller;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .encryptedPassword("$2a$10$encodedPassword")
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void signIn_shouldReturnTokenOnSuccessfulLogin() {
        String expectedToken = "jwt.token.here";
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getEncryptedPassword())).thenReturn(true);
        when(jwtUtil.encode("test@example.com")).thenReturn(expectedToken);

        ResponseEntity<?> response = controller.signIn(loginRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals(expectedToken, body.get("access_token"));
        
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", testUser.getEncryptedPassword());
        verify(jwtUtil).encode("test@example.com");
    }

    @Test
    void signIn_shouldReturn401WhenUserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.signIn(loginRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("invalid email or password", body.get("message"));
        
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).encode(anyString());
    }

    @Test
    void signIn_shouldReturn401WhenPasswordDoesNotMatch() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getEncryptedPassword())).thenReturn(false);

        ResponseEntity<?> response = controller.signIn(loginRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("invalid email or password", body.get("message"));
        
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", testUser.getEncryptedPassword());
        verify(jwtUtil, never()).encode(anyString());
    }

    @Test
    void signIn_shouldHandleNullPassword() {
        loginRequest.setPassword(null);
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(null, testUser.getEncryptedPassword())).thenReturn(false);

        ResponseEntity<?> response = controller.signIn(loginRequest);

        assertEquals(401, response.getStatusCodeValue());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void signIn_shouldHandleEmptyEmail() {
        loginRequest.setEmail("");
        
        when(userRepository.findByEmail("")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.signIn(loginRequest);

        assertEquals(401, response.getStatusCodeValue());
        verify(userRepository).findByEmail("");
    }
}