package com.example.jobportal.service;

import com.example.jobportal.entity.*;
import com.example.jobportal.enums.ApplicationStatus;
import com.example.jobportal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    // Apply job
    public void applyJob(Long userId, Long jobId, String resumeUrl, String publicId, String coverLetter) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Prevent duplicate applications
        if (applicationRepository.existsByJobAndSeeker(job, user)) {
            throw new RuntimeException("Already applied for this job");
        }

        JobApplication application = JobApplication.builder()
                .seeker(user)
                .job(job)
                .resumeUrl(resumeUrl)
                .resumePublicId(publicId)
                .coverLetter(coverLetter)
                .status(ApplicationStatus.APPLIED)
                .appliedAt(new Date())
                .build();

        applicationRepository.save(application);
    }

    // View all applications for a job
    public List<JobApplication> getApplicationsForJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return applicationRepository.findByJob(job);
    }

    // View all applications by a seeker
    public List<JobApplication> getApplicationsBySeeker(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return applicationRepository.findBySeeker(user);
    }
}