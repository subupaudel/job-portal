package com.example.jobportal.controller;

import com.example.jobportal.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;


    @GetMapping
    public ResponseEntity<?> getAllJobs() {

        var jobs = jobService.getAllOpenJobs();

        return ResponseEntity.ok(jobs);
    }

}
