package com.adminapplicationmaster.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adminapplicationmaster.domain.entity.User;
import com.adminapplicationmaster.dto.LoginRequest;
import com.adminapplicationmaster.repository.UserRepository;
import com.adminapplicationmaster.util.JwtUtil;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Authentication Controller replacing Users::SessionsController
 */
@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/sign_in")
    public ResponseEntity<?> signIn(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
        
        if (user == null) {
            log.warn("User not found: {}", loginRequest.getEmail());
            return ResponseEntity.status(401).body(Map.of("message", "invalid email or password"));
        }
        
        log.info("User found. Checking password...");
        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getEncryptedPassword());
        log.info("Password matches: {}", passwordMatches);
        
        if (passwordMatches) {
            String token = jwtUtil.encode(user.getEmail());
            return ResponseEntity.ok(Map.of("access_token", token));
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "invalid email or password"));
        }
    }
}