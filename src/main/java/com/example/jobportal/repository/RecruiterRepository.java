package com.example.jobportal.repository;

import com.example.jobportal.entity.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {

    Optional<Recruiter> findByUserId(Long userId);
    Recruiter getRecruiterByUserId(Long userId);

}
