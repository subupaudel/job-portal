package com.example.jobportal.service.impl;

import com.example.jobportal.dto.JobRequest;
import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.entity.Job;
import com.example.jobportal.entity.Recruiter;
import com.example.jobportal.entity.RecruiterPlan;
import com.example.jobportal.enums.JobStatus;
import com.example.jobportal.exception.JobException;
import com.example.jobportal.repository.JobRepository;
import com.example.jobportal.repository.RecruiterPlanRepository;
import com.example.jobportal.repository.RecruiterRepository;
import com.example.jobportal.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final RecruiterPlanRepository recruiterPlanRepository;

    @Override
    public JobResponse createJob(Recruiter recruiter, JobRequest request) {

        if (recruiter.isBlocked()) {
            throw JobException.unauthorized("You are blocked and cannot create jobs");
        }

        RecruiterPlan plan = recruiterPlanRepository
                .findTopByRecruiterIdAndExpiryDateAfterOrderByExpiryDateDesc(
                        recruiter.getId(), LocalDate.now())
                .orElseThrow(() -> JobException.notFound("No active subscription plan found"));

        if (plan.getJobsUsed() >= plan.getJobLimit()) {
            throw JobException.badRequest("You have reached your job posting limit");
        }

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .salary(request.getSalary())
                .status(request.getStatus() != null ? request.getStatus() : JobStatus.OPEN)
                .recruiter(recruiter)
                .build();

        Job saved = jobRepository.save(job);

        plan.setJobsUsed(plan.getJobsUsed() + 1);
        recruiterPlanRepository.save(plan);

        return mapToResponse(saved);
    }

    @Override
    public List<JobResponse> getJobsByRecruiter(Recruiter recruiter) {
        return jobRepository.findByRecruiter(recruiter)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public JobResponse updateJob(Long jobId, Long userId, JobRequest request) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> JobException.notFound("Job not found"));

        if (!job.getRecruiter().getUser().getId().equals(userId)) {
            throw JobException.unauthorized("You are not allowed to update this job");
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());

        if (request.getStatus() != null) {
            job.setStatus(request.getStatus());
        }

        return mapToResponse(jobRepository.save(job));
    }

    @Override
    public void deleteJob(Long jobId, Long userId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> JobException.notFound("Job not found"));

        if (!job.getRecruiter().getUser().getId().equals(userId)) {
            throw JobException.unauthorized("You are not allowed to delete this job");
        }

        jobRepository.delete(job);
    }

    @Override
    public List<JobResponse> searchJobs(String keyword, String location, Double minSalary) {

        return jobRepository.findAll().stream()
                .filter(j -> keyword == null ||
                        j.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        j.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .filter(j -> location == null || j.getLocation().toLowerCase().contains(location.toLowerCase()))
                .filter(j -> minSalary == null || j.getSalary().doubleValue() >= minSalary)
                .map(this::mapToResponse)
                .toList();
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