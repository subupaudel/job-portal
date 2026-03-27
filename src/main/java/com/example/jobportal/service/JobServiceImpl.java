package com.example.jobportal.service;

import com.example.jobportal.dto.JobRequest;
import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.entity.Job;
import com.example.jobportal.entity.Recruiter;
import com.example.jobportal.entity.RecruiterPlan;
import com.example.jobportal.enums.JobStatus;
import com.example.jobportal.repository.JobRepository;
import com.example.jobportal.repository.RecruiterPlanRepository;
import com.example.jobportal.repository.RecruiterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final RecruiterRepository recruiterRepository;
    private final RecruiterPlanRepository recruiterPlanRepository;

    @Override
    public JobResponse createJob(Recruiter recruiter, JobRequest request) {

        if (recruiter.isBlocked()) {
            throw new RuntimeException("You are blocked and cannot create jobs");
        }

        RecruiterPlan activePlan = recruiterPlanRepository
                .findTopByRecruiterIdAndExpiryDateAfterOrderByExpiryDateDesc(recruiter.getId(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("No active plan. Please purchase a package."));

        if (activePlan.getJobsUsed() >= activePlan.getJobLimit()) {
            throw new RuntimeException("Job posting limit reached. Upgrade your plan.");
        }

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .salary(request.getSalary())
                .status(request.getStatus() != null ? request.getStatus() : JobStatus.OPEN)
                .recruiter(recruiter)
                .build();

        Job savedJob = jobRepository.save(job);

        activePlan.setJobsUsed(activePlan.getJobsUsed() + 1);
        recruiterPlanRepository.save(activePlan);

        return mapToResponse(savedJob);
    }

    @Override
    public List<JobResponse> getJobsByRecruiter(Recruiter recruiter) {
        return jobRepository.findByRecruiter(recruiter)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobResponse updateJob(Long jobId, Long userId, JobRequest request) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getRecruiter().getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to update this job");
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());

        if (request.getStatus() != null) {
            job.setStatus(request.getStatus());
        }

        Job updatedJob = jobRepository.save(job);
        return mapToResponse(updatedJob);
    }

    private JobResponse mapToResponse(Job job) {

        Long recruiterId = job.getRecruiter().getId();
        Recruiter recruiter = recruiterRepository.findByUserId(recruiterId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .salary(job.getSalary())
                .status(job.getStatus())
                .recruiterId(job.getRecruiter().getId())
                .recruiterName(recruiter.getCompanyName())
                .build();
    }
    @Override
    public void deleteJob(Long jobId, Long userId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getRecruiter().getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to delete this job");
        }

        jobRepository.delete(job);
    }

    @Override
    public List<JobResponse> searchJobs(String keyword, String location, Double minSalary) {

        List<Job> jobs = jobRepository.findAll();

        return jobs.stream()
                .filter(job -> matchesKeyword(job, keyword))
                .filter(job -> matchesLocation(job, location))
                .filter(job -> matchesSalary(job, minSalary))
                .map(this::mapToResponse)
                .toList();
    }

    private boolean matchesKeyword(Job job, String keyword) {
        if (keyword == null || keyword.isEmpty()) return true;

        String lowerKeyword = keyword.toLowerCase();

        return job.getTitle().toLowerCase().contains(lowerKeyword) ||
                job.getDescription().toLowerCase().contains(lowerKeyword);
    }

    private boolean matchesLocation(Job job, String location) {
        if (location == null || location.isEmpty()) return true;

        return job.getLocation().toLowerCase()
                .contains(location.toLowerCase());
    }

    private boolean matchesSalary(Job job, Double minSalary) {
        if (minSalary == null) return true;

        return job.getSalary().doubleValue() >= minSalary;
    }
}
