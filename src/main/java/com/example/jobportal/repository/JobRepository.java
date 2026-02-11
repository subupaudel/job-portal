package com.example.jobportal.repository;

import com.example.jobportal.entity.Job;
import com.example.jobportal.entity.Recruiter;
import com.example.jobportal.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByRecruiter(Recruiter recruiter);

    Collection<Object> findByStatus(JobStatus jobStatus);
}
