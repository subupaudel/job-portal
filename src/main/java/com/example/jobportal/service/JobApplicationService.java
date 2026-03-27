package com.example.jobportal.service;

import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.entity.*;
import com.example.jobportal.enums.ApplicationStatus;
import com.example.jobportal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.regex.Pattern.matches;

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

    public List<JobResponse> getRecommendedJobs(Long userId) {

        // 1️⃣ Get seeker
        Seeker seeker = seekerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seeker not found"));

        String skills = seeker.getSkills(); // "Java, Spring Boot"
        String qualification = seeker.getQualification().name();

        // 2️⃣ Split skills
        List<String> skillList = Arrays.stream(skills.split(","))
                .map(String::trim)
                .toList();

        // 3️⃣ Fetch all jobs
        List<Job> jobs = jobRepository.findAll();

        // 4️⃣ Filter jobs based on matching
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