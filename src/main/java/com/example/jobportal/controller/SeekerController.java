package com.example.jobportal.controller;

import com.example.jobportal.cloudinary.CloudinaryService;
import com.example.jobportal.dto.ProfileResponse;
import com.example.jobportal.dto.SeekerRequest;
import com.example.jobportal.dto.SeekerResponse;
import com.example.jobportal.security.JwtUtil;
import com.example.jobportal.service.JobApplicationService;
import com.example.jobportal.service.JobService;
import com.example.jobportal.service.RecruiterService;
import com.example.jobportal.service.SeekerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/seeker")
@RequiredArgsConstructor
public class SeekerController {

    private final JwtUtil jwtUtil;
    private final SeekerService seekerService;
    private final RecruiterService recruiterService;
    private final JobService jobService;

    private final CloudinaryService cloudinaryService;
    private final JobApplicationService jobApplicationService;


    @PostMapping("/profile")
    public ResponseEntity<SeekerResponse> update(
            @RequestBody SeekerRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(authHeader));
        return ResponseEntity.ok(seekerService.createProfile(userId, request));
    }

    @GetMapping("/profile")
    public SeekerResponse getProfile(@RequestHeader("Authorization") String authHeader) {

        Long userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(authHeader));
        return seekerService.getProfile(userId);
    }

    @PutMapping("/profile")
    public ResponseEntity<SeekerResponse> updateProfile(
            @RequestBody SeekerRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(authHeader));
        return ResponseEntity.ok(seekerService.createProfile(userId, request));
    }


    @PostMapping(value = "/apply/{jobId}", consumes = "multipart/form-data")
    public ResponseEntity<?> applyJob(
            @PathVariable Long jobId,
            @RequestParam("resume") MultipartFile resume,
            @RequestParam(value = "coverLetter", required = false) String coverLetter,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(authHeader));

        if (resume == null || resume.isEmpty()) {
            return ResponseEntity.badRequest().body("Resume file is required");
        }

        String result = cloudinaryService.uploadPDF(resume);
        String[] parts = result.split("\\|");

        String resumeUrl = parts[0];
        String publicId = parts[1];

        jobApplicationService.applyJob(userId, jobId, resumeUrl, publicId, coverLetter);

        return ResponseEntity.ok("Job applied successfully");
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getMyApplications(@RequestHeader("Authorization") String authHeader) {

        Long userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(authHeader));

        Long seekerId = seekerService.getSeekerIdByUserId(userId);

        var applications = jobApplicationService.getApplicationsBySeeker(seekerId);
        return ResponseEntity.ok(applications);
    }


    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<ProfileResponse> viewRecruiterProfile(
            @PathVariable Long recruiterId) {

        return ResponseEntity.ok(recruiterService.getRecruiterProfile(recruiterId));
    }


    @PostMapping("/report/{recruiterId}")
    public ResponseEntity<String> reportRecruiter(
            @PathVariable Long recruiterId,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(authHeader));
        seekerService.reportRecruiter(userId, recruiterId);

        return ResponseEntity.ok("Recruiter reported successfully");
    }

    @GetMapping("/recommended-jobs")
    public ResponseEntity<?> getRecommendedJobs(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(authHeader));

        var jobs = jobApplicationService.getRecommendedJobs(userId);

        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minSalary) {

        var jobs = jobService.searchJobs(keyword, location, minSalary);

        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/jobs")
    public ResponseEntity<?> getAllJobs() {

        var jobs = jobService.getAllOpenJobs();

        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<?> getJobById(@PathVariable Long jobId) {

        var job = jobService.getJobById(jobId);

        return ResponseEntity.ok(job);
    }

}