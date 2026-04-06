package com.example.jobportal.service.impl;

import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.entity.*;
import com.example.jobportal.enums.ApplicationStatus;
import com.example.jobportal.exception.JobException;
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
    private final RecruiterRepository recruiterRepository;

    // ---------------- APPLY JOB ----------------
    @Override
    public void applyJob(Long userId, Long jobId, String resumeUrl, String publicId, String coverLetter) {

        Seeker seeker = seekerRepository.findByUserId(userId)
                .orElseThrow(() -> JobException.notFound("Seeker profile not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> JobException.notFound("Job not found"));

        Recruiter recruiter = job.getRecruiter();

        if (applicationRepository.existsByJobAndSeeker(job, seeker)) {
            throw JobException.badRequest("You have already applied for this job");
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

    // ---------------- GET APPLICATIONS BY SEEKER ----------------
    @Override
    public List<JobApplication> getApplicationsBySeeker(Long seekerId) {
        Seeker seeker = seekerRepository.findById(seekerId)
                .orElseThrow(() -> JobException.notFound("Seeker not found"));
        return applicationRepository.findBySeeker(seeker);
    }

    // ---------------- RECOMMENDED JOBS ----------------
    @Override
    public List<JobResponse> getRecommendedJobs(Long userId) {

        Seeker seeker = seekerRepository.findByUserId(userId)
                .orElseThrow(() -> JobException.notFound("Seeker not found"));

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
            if (title.contains(skill.toLowerCase()) || description.contains(skill.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<JobApplication> getApplicationsForRecruiter(Long userId) {

        Recruiter recruiter = recruiterRepository.getRecruiterByUserId(userId);

        if (recruiter == null) {
            throw JobException.notFound("Recruiter not found");
        }

        return applicationRepository.findByRecruiter(recruiter);
    }

    @Override
    public List<JobApplication> getApplicationsForRecruiterJob(Long jobId, Long userId) {

        // ✅ Get recruiter from logged-in user
        Recruiter recruiter = recruiterRepository.getRecruiterByUserId(userId);

        if (recruiter == null) {
            throw JobException.notFound("Recruiter not found");
        }

        // ✅ Get job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> JobException.notFound("Job not found"));

        // 🔒 Security check (VERY IMPORTANT)
        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw JobException.badRequest("You are not allowed to view these applications");
        }

        // ✅ Fetch applications for this job only
        return applicationRepository.findByJob(job);
    }

    @Override
    public void updateApplicationStatus(Long applicationId,
                                        Long recruiterUserId,
                                        ApplicationStatus status,
                                        Date interviewDate) {

        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> JobException.notFound("Application not found"));

        // 🔒 Security: Only job owner can update
        if (!application.getRecruiter().getUser().getId().equals(recruiterUserId)) {
            throw JobException.badRequest("You are not authorized to update this application");
        }

        // ✅ If shortlisted → interview date required
        if (status == ApplicationStatus.SHORTLISTED) {
            if (interviewDate == null) {
                throw JobException.badRequest("Interview date is required for shortlisted candidates");
            }
            application.setInterviewDate(interviewDate);
        }

        // ❌ If rejected → clear interview date
        if (status == ApplicationStatus.REJECTED) {
            application.setInterviewDate(null);
        }

        application.setStatus(status);

        applicationRepository.save(application);
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