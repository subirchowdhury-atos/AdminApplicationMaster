package com.adminapplicationmaster.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/hash")
    public String testHash() {
        String plainPassword = "password123";
        String hashedFromDb = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        boolean matches = passwordEncoder.matches(plainPassword, hashedFromDb);
        
        // Generate a new hash
        String newHash = passwordEncoder.encode(plainPassword);
        
        return String.format("Matches: %s\nNew hash: %s\nDB hash: %s", 
            matches, newHash, hashedFromDb);
    }
}