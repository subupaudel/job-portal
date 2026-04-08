package com.example.jobportal.controller;

import com.example.jobportal.dto.JobRequest;
import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.entity.JobApplication;
import com.example.jobportal.entity.Recruiter;
import com.example.jobportal.repository.RecruiterRepository;
import com.example.jobportal.security.JwtUtil;
import com.example.jobportal.service.JobApplicationService;
import com.example.jobportal.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final JwtUtil jwtUtil;
    private final RecruiterRepository recruiterRepository;
    private final JobApplicationService jobApplicationService;

    @GetMapping
    public ResponseEntity<?> getAllJobs() {

        var jobs = jobService.getAllOpenJobs();

        return ResponseEntity.ok(jobs);
    }

    @PreAuthorize("hasRole('JOB_RECRUITER')")
    @PostMapping("/jobs")
    public ResponseEntity<JobResponse> createJob(
            @RequestBody JobRequest jobRequest,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        Recruiter recruiter = recruiterRepository.getRecruiterByUserId(userId);

        JobResponse createdJob = jobService.createJob(recruiter, jobRequest);
        return new ResponseEntity<>(createdJob, HttpStatus.CREATED);
    }

    @GetMapping("/jobs")
    @PreAuthorize("hasRole('JOB_RECRUITER')")
    public ResponseEntity<List<JobResponse>> getOwnJobs(
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        Recruiter recruiter = recruiterRepository.getRecruiterByUserId(userId);

        List<JobResponse> jobs = jobService.getJobsByRecruiter(recruiter);
        return ResponseEntity.ok(jobs);
    }

    @PutMapping("/jobs/{jobId}")
    @PreAuthorize("hasRole('JOB_RECRUITER')")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long jobId,
            @RequestBody JobRequest jobRequest,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        JobResponse updatedJob =
                jobService.updateJob(jobId, userId, jobRequest);

        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/jobs/{jobId}")
    @PreAuthorize("hasRole('JOB_RECRUITER')")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long jobId,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        jobService.deleteJob(jobId, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs/{jobId}/applications")
    @PreAuthorize("hasRole('JOB_RECRUITER')")
    public ResponseEntity<List<JobApplication>> getApplicationsForJob(
            @PathVariable Long jobId,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<JobApplication> applications =
                jobApplicationService.getApplicationsForRecruiterJob(jobId, userId);

        return ResponseEntity.ok(applications);
    }

    @GetMapping("/jobs/{jobId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<?> getJobById(@PathVariable Long jobId) {

        var job = jobService.getJobById(jobId);

        return ResponseEntity.ok(job);
    }

}
