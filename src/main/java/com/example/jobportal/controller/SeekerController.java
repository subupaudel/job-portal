package com.example.jobportal.controller;

import com.example.jobportal.dto.JobResponse;
import com.example.jobportal.dto.ProfileResponse;
import com.example.jobportal.dto.SeekerRequest;
import com.example.jobportal.dto.SeekerResponse;
import com.example.jobportal.security.JwtUtil;
import com.example.jobportal.service.JobService;
import com.example.jobportal.service.RecruiterService;
import com.example.jobportal.service.SeekerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seeker")
@RequiredArgsConstructor
public class SeekerController {

    private final JwtUtil jwtUtil;
    private final SeekerService seekerService;
    private final JobService jobService;
    private final RecruiterService  recruiterService;

    @PostMapping("/profile")
    public ResponseEntity<SeekerResponse> update(
            @RequestBody SeekerRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        SeekerResponse response = seekerService.createProfile(userId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public SeekerResponse getProfile(@RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);

        Long userId = jwtUtil.getUserIdFromToken(token);

        return seekerService.getProfile(userId);
    }

    @PutMapping("/profile")
    public ResponseEntity<SeekerResponse> updateProfile(
            @RequestBody SeekerRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        SeekerResponse response = seekerService.createProfile(userId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<ProfileResponse> viewRecruiterProfile(
            @PathVariable Long recruiterId) {

        ProfileResponse response =
                recruiterService.getRecruiterProfile(recruiterId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/report/{recruiterId}")
    public ResponseEntity<String> reportRecruiter(
            @PathVariable Long recruiterId,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        seekerService.reportRecruiter(userId, recruiterId);

        return ResponseEntity.ok("Recruiter reported successfully");
    }

}
