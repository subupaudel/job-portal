package com.example.jobportal.repository;

import com.example.jobportal.entity.RecruiterPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface RecruiterPlanRepository extends JpaRepository<RecruiterPlan, Long> {
    Optional<RecruiterPlan> findTopByRecruiterIdAndExpiryDateAfterOrderByExpiryDateDesc(Long recruiterId, LocalDate date);
}
