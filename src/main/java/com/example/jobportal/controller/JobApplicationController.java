package com.example.jobportal.controller;

import com.example.jobportal.entity.JobApplication;
import com.example.jobportal.cloudinary.CloudinaryService;
import com.example.jobportal.service.JobApplicationService;
import com.example.jobportal.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JwtUtil jwtUtil;
    private final JobApplicationService jobApplicationService;
    private final CloudinaryService cloudinaryService;

    // Apply for job
    @PostMapping(value = "/apply/{jobId}", consumes = "multipart/form-data")
    public ResponseEntity<?> applyJob(
            @PathVariable Long jobId,
            @RequestParam("resume") MultipartFile resume,
            @RequestParam(value = "coverLetter", required = false) String coverLetter,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(authHeader));

        String result = cloudinaryService.uploadPDF(resume);
        String[] parts = result.split("\\|");
        String resumeUrl = parts[0];
        String publicId = parts[1];

        jobApplicationService.applyJob(userId, jobId, resumeUrl, publicId, coverLetter);

        return ResponseEntity.ok("Job applied successfully");
    }

    // Get applications by job (recruiter)
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<JobApplication>> getApplicationsForJob(@PathVariable Long jobId) {
        List<JobApplication> applications = jobApplicationService.getApplicationsForJob(jobId);
        return ResponseEntity.ok(applications);
    }

    // Get applications by seeker
    @GetMapping("/me")
    public ResponseEntity<List<JobApplication>> getMyApplications(@RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(authHeader));
        List<JobApplication> applications = jobApplicationService.getApplicationsBySeeker(userId);
        return ResponseEntity.ok(applications);
    }
}