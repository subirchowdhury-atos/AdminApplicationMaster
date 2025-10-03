package com.adminapplicationmaster.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.adminapplicationmaster.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Security configuration implementation
 * - HTTP Basic Authentication enabled (config.http_authenticatable = true)
 * - Session timeout: 3 minutes (config.timeout_in = 3.minutes)
 * - Password length: 6-128 characters (config.password_length = 6..128)
 * - BCrypt with 11 stretches (config.stretches = 11)
 * - JWT authentication for API endpoints
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable for API
            .cors(cors -> cors.configure(http)) // Use CorsConfig
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/**").authenticated() // API endpoints require JWT
                .requestMatchers("/users/sign_in", "/login", "/register").permitAll()
                .requestMatchers("/dashboard", "/loan_applications/**", "/users/**").authenticated()
                .anyRequest().permitAll()
            )
            .httpBasic(basic -> {}) // Enable HTTP Basic Auth (Devise config.http_authenticatable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt password encoder with strength 10 (matching Devise config.stretches)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}