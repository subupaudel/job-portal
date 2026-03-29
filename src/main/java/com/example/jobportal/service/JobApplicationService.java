package com.example.jobportal.service;

import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.entity.JobApplication;

import java.util.List;

public interface JobApplicationService {

    void applyJob(Long userId, Long jobId, String resumeUrl, String publicId, String coverLetter);

    List<JobApplication> getApplicationsForJob(Long jobId);

    List<JobApplication> getApplicationsBySeeker(Long seekerId);

    List<JobResponse> getRecommendedJobs(Long userId);
}