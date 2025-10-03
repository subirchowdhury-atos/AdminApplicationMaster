package com.adminapplicationmaster.repository;

import com.adminapplicationmaster.domain.entity.LoanApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    Page<LoanApplication> findByStatusIn(List<String> statuses, Pageable pageable);
}