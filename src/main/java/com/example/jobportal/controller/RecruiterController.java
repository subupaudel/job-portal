package com.example.jobportal.controller;

import com.example.jobportal.dto.JobRequest;
import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.dto.RecruiterRequest;
import com.example.jobportal.dto.ProfileResponse;
import com.example.jobportal.entity.Recruiter;
import com.example.jobportal.repository.RecruiterRepository;
import com.example.jobportal.security.JwtUtil;
import com.example.jobportal.service.JobService;
import com.example.jobportal.service.RecruiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recruiter")
@RequiredArgsConstructor
public class RecruiterController {

    private final RecruiterService recruiterService;
    private final JobService jobService;
    private final JwtUtil jwtUtil;
    private final RecruiterRepository recruiterRepository;

    @PostMapping(value = "/profile", consumes = "multipart/form-data")
    public ProfileResponse createProfile(
            @ModelAttribute RecruiterRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        return recruiterService.updateProfile(userId, request);
    }

    @PutMapping(value = "/profile", consumes = "multipart/form-data")
    public ProfileResponse update(
            @ModelAttribute RecruiterRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);
        return recruiterService.updateProfile(userId, request);
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(@RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);

        Long userId = jwtUtil.getUserIdFromToken(token);

        return recruiterService.getProfile(userId);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfile(
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        recruiterService.deleteProfile(userId);

        return ResponseEntity.ok("Recruiter profile deleted successfully");
    }


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
    public ResponseEntity<List<JobResponse>> getOwnJobs(
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        Recruiter recruiter = recruiterRepository.getRecruiterByUserId(userId);

        List<JobResponse> jobs = jobService.getJobsByRecruiter(recruiter);
        return ResponseEntity.ok(jobs);
    }

    @PutMapping("/jobs/{jobId}")
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
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long jobId,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        jobService.deleteJob(jobId, userId);

        return ResponseEntity.noContent().build();
    }

}
