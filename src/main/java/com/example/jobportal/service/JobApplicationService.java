package com.example.jobportal.service;

import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.entity.JobApplication;
import com.example.jobportal.enums.ApplicationStatus;

import java.util.Date;
import java.util.List;

public interface JobApplicationService {

    void applyJob(Long userId, Long jobId, String resumeUrl, String publicId, String coverLetter);

    List<JobApplication> getApplicationsBySeeker(Long seekerId);

    List<JobResponse> getRecommendedJobs(Long userId);

    List<JobApplication> getApplicationsForRecruiter(Long userId);

    List<JobApplication> getApplicationsForRecruiterJob(Long jobId, Long userId);

    void updateApplicationStatus(Long applicationId, Long recruiterUserId,
                                 ApplicationStatus status, Date interviewDate);
}