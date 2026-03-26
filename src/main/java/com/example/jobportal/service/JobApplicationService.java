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
    private final SeekerRepository seekerRepository;

    // Apply job
    public void applyJob(Long userId,
                         Long jobId,
                         String resumeUrl,
                         String publicId,
                         String coverLetter) {

        // ✅ Get seeker
        Seeker seeker = seekerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seeker profile not found"));

        // ✅ Get job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // ✅ Get recruiter from job
        Recruiter recruiter = job.getRecruiter();

        // ❌ Prevent duplicate
        if (applicationRepository.existsByJobAndSeeker(job, seeker)) {
            throw new RuntimeException("Already applied");
        }

        JobApplication application = JobApplication.builder()
                .seeker(seeker)
                .job(job)
                .recruiter(recruiter) // ✅ NEW
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
    public List<JobApplication> getApplicationsBySeeker(Long seekerId) {
        Seeker seeker = seekerRepository.findById(seekerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return applicationRepository.findBySeeker(seeker);
    }
}