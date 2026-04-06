package com.example.jobportal.repository;

import com.example.jobportal.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByJobAndSeeker(Job job, Seeker seeker);

    List<JobApplication> findByJob(Job job);

    List<JobApplication> findBySeeker(Seeker seeker);
    List<JobApplication> findByRecruiter(Recruiter recruiter);
    void deleteByJob(Job job);


}