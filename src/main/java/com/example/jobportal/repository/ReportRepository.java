package com.example.jobportal.repository;

import com.example.jobportal.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsBySeeker_IdAndRecruiter_Id(Long seekerId, Long recruiterId);

    int countByRecruiter_Id(Long recruiterId);

}