package com.adminapplicationmaster.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    //@NotBlank
    @Column(name = "encrypted_password", nullable = false)
    private String encryptedPassword;

    @NotBlank(message = "Contact is required")
    @Column(name = "contact", nullable = false)
    private String contact;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_sent_at")
    private LocalDateTime resetPasswordSentAt;

    @Column(name = "remember_created_at")
    private LocalDateTime rememberCreatedAt;

    @Column(name = "sign_in_count", nullable = false)
    @Builder.Default
    private Integer signInCount = 0;

    @Column(name = "current_sign_in_at")
    private LocalDateTime currentSignInAt;

    @Column(name = "last_sign_in_at")
    private LocalDateTime lastSignInAt;

    @Column(name = "current_sign_in_ip")
    private String currentSignInIp;

    @Column(name = "last_sign_in_ip")
    private String lastSignInIp;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "role")
    private String role;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "authentication_token", unique = true, length = 30)
    private String authenticationToken;

}