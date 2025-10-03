package com.adminapplicationmaster.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adminapplicationmaster.domain.entity.User;
import com.adminapplicationmaster.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * REST API Controller for User Management
 * Requires JWT authentication
 */
@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class ApiUsersController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<User>> index() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return userRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(Map.of("message", "User not found")));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user) {
        try {
            // Set default password if not provided
            if (user.getEncryptedPassword() == null || user.getEncryptedPassword().isEmpty()) {
                user.setEncryptedPassword(passwordEncoder.encode("12345678"));
            } else {
                user.setEncryptedPassword(passwordEncoder.encode(user.getEncryptedPassword()));
            }
            
            User saved = userRepository.save(user);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error creating user", e);
            return ResponseEntity.unprocessableEntity()
                    .body(Map.of("errors", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody User user) {
        return userRepository.findById(id)
                .<ResponseEntity<?>>map(existing -> {
                    existing.setEmail(user.getEmail());
                    existing.setFirstName(user.getFirstName());
                    existing.setLastName(user.getLastName());
                    existing.setRole(user.getRole());
                    existing.setContact(user.getContact());
                    
                    // Only update password if provided
                    if (user.getEncryptedPassword() != null && !user.getEncryptedPassword().isEmpty()) {
                        existing.setEncryptedPassword(passwordEncoder.encode(user.getEncryptedPassword()));
                    }
                    
                    User updated = userRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(Map.of("message", "User not found")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return userRepository.findById(id)
                .<ResponseEntity<?>>map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
                })
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(Map.of("message", "User not found")));
    }
}