package com.example.jobportal.service;

import com.example.jobportal.dto.JobRequest;
import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.entity.Recruiter;

import java.util.List;

public interface JobService {
    JobResponse createJob(Recruiter recruiter, JobRequest jobRequest);
    List<JobResponse> getJobsByRecruiter(Recruiter recruiter);

    JobResponse updateJob(Long jobId, Long userId, JobRequest jobRequest);

    void deleteJob(Long jobId, Long userId);

    List<JobResponse> getAllJobs();
}
