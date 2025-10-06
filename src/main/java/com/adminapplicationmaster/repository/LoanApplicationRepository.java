package com.adminapplicationmaster.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.adminapplicationmaster.domain.entity.LoanApplication;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    Page<LoanApplication> findByStatusIn(List<String> statuses, Pageable pageable);

    Page<LoanApplication> findByStatus(String status, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE loan_applications SET ssn = :ssn WHERE id = :id", nativeQuery = true)
    void updateSsnById(@Param("id") Long id, @Param("ssn") String ssn);
}