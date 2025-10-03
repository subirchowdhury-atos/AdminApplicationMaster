package com.adminapplicationmaster.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "application_decisions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ApplicationDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;

    @Column(name = "encrypted_request", columnDefinition = "TEXT")
    @Convert(converter = com.adminapplicationmaster.config.EncryptionConverter.class)
    private String request;

    @Column(name = "encrypted_request_iv")
    private String requestIv;

    @Column(name = "encrypted_response", columnDefinition = "TEXT")
    @Convert(converter = com.adminapplicationmaster.config.EncryptionConverter.class)
    private String response;

    @Column(name = "encrypted_response_iv")
    private String responseIv;

    @Column(name = "decision")
    private String decision;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PostPersist
    @PostUpdate
    public void setApplicationStatus() {
        if ("eligible".equals(this.decision)) {
            this.loanApplication.setStatus("approved");
        } else if ("decline".equals(this.decision)) {
            this.loanApplication.setStatus("rejected");
        }
    }

}