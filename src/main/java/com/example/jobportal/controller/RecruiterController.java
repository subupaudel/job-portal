package com.example.jobportal.controller;

import com.example.jobportal.dto.*;
import com.example.jobportal.entity.JobApplication;
import com.example.jobportal.entity.Recruiter;
import com.example.jobportal.entity.RecruiterPlan;
import com.example.jobportal.repository.RecruiterRepository;
import com.example.jobportal.security.JwtUtil;
import com.example.jobportal.service.JobApplicationService;
import com.example.jobportal.service.JobService;
import com.example.jobportal.service.RecruiterPlanService;
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
    private final RecruiterPlanService recruiterPlanService;
    private final JobApplicationService jobApplicationService;


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

    @GetMapping("/subscription")
    public ResponseEntity<RecruiterPlan> getMySubscription(
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        Recruiter recruiter = recruiterRepository.getRecruiterByUserId(userId);

        RecruiterPlan plan = recruiterPlanService.getCurrentPlan(recruiter.getId());

        return ResponseEntity.ok(plan);
    }

    @GetMapping("/applications")
    public ResponseEntity<List<JobApplication>> getMyApplications(
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<JobApplication> applications =
                jobApplicationService.getApplicationsForRecruiter(userId);

        return ResponseEntity.ok(applications);
    }

    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<String> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody ApplicationStatusUpdateRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        jobApplicationService.updateApplicationStatus(
                applicationId,
                userId,
                request.getStatus(),
                request.getInterviewDate()
        );

        return ResponseEntity.ok("Application status updated successfully");
    }

}
