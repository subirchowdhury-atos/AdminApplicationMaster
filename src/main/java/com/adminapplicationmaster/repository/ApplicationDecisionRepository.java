package com.adminapplicationmaster.repository;

import com.adminapplicationmaster.domain.entity.ApplicationDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationDecisionRepository extends JpaRepository<ApplicationDecision, Long> {
}