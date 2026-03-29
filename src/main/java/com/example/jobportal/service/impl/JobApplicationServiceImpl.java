package com.example.jobportal.service.impl;

import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.entity.*;
import com.example.jobportal.enums.ApplicationStatus;
import com.example.jobportal.repository.*;
import com.example.jobportal.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final SeekerRepository seekerRepository;

    // ---------------- APPLY JOB ----------------
    @Override
    public void applyJob(Long userId, Long jobId, String resumeUrl, String publicId, String coverLetter) {

        Seeker seeker = seekerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seeker profile not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        Recruiter recruiter = job.getRecruiter();

        if (applicationRepository.existsByJobAndSeeker(job, seeker)) {
            throw new RuntimeException("Already applied");
        }

        JobApplication application = JobApplication.builder()
                .seeker(seeker)
                .job(job)
                .recruiter(recruiter)
                .resumeUrl(resumeUrl)
                .resumePublicId(publicId)
                .coverLetter(coverLetter)
                .status(ApplicationStatus.APPLIED)
                .appliedAt(new Date())
                .build();

        applicationRepository.save(application);
    }

    // ---------------- GET APPLICATIONS BY JOB ----------------
    @Override
    public List<JobApplication> getApplicationsForJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return applicationRepository.findByJob(job);
    }

    // ---------------- GET APPLICATIONS BY SEEKER ----------------
    @Override
    public List<JobApplication> getApplicationsBySeeker(Long seekerId) {
        Seeker seeker = seekerRepository.findById(seekerId)
                .orElseThrow(() -> new RuntimeException("Seeker not found"));
        return applicationRepository.findBySeeker(seeker);
    }

    // ---------------- RECOMMENDED JOBS ----------------
    @Override
    public List<JobResponse> getRecommendedJobs(Long userId) {

        Seeker seeker = seekerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seeker not found"));

        String skills = seeker.getSkills();
        String qualification = seeker.getQualification().name();

        List<String> skillList = Arrays.stream(skills.split(","))
                .map(String::trim)
                .toList();

        List<Job> jobs = jobRepository.findAll();

        return jobs.stream()
                .filter(job -> matches(job, skillList, qualification))
                .map(this::mapToResponse)
                .toList();
    }

    private boolean matches(Job job, List<String> skills, String qualification) {

        String title = job.getTitle().toLowerCase();
        String description = job.getDescription().toLowerCase();

        for (String skill : skills) {
            if (title.contains(skill.toLowerCase()) ||
                    description.contains(skill.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private JobResponse mapToResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .salary(job.getSalary())
                .status(job.getStatus())
                .recruiterId(job.getRecruiter().getId())
                .recruiterName(job.getRecruiter().getCompanyName())
                .build();
    }
}