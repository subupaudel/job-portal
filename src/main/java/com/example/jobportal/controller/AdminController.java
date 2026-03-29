package com.example.jobportal.controller;

import com.example.jobportal.security.JwtUtil;
import com.example.jobportal.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    // ---------------- DELETE OWN ACCOUNT ----------------
    @DeleteMapping
    public ResponseEntity<String> deleteOwnAccount(
            @RequestHeader("Authorization") String authHeader) {

        Long adminUserId = jwtUtil.getUserIdFromToken(jwtUtil.extractToken(authHeader));

        adminService.deleteAdminAccount(adminUserId);

        return ResponseEntity.ok("Admin account deleted successfully");
    }

    // ---------------- RESET RECRUITER REPORT ----------------
    @PutMapping("/recruiter/{recruiterId}/reset-report")
    public ResponseEntity<String> resetRecruiterReport(@PathVariable Long recruiterId) {

        adminService.resetRecruiterReport(recruiterId);

        return ResponseEntity.ok("Recruiter report reset to 0");
    }

    // ---------------- DELETE RECRUITER ----------------
    @DeleteMapping("/recruiter/{recruiterId}")
    public ResponseEntity<String> deleteRecruiter(@PathVariable Long recruiterId) {

        adminService.deleteRecruiter(recruiterId);

        return ResponseEntity.ok("Recruiter deleted successfully");
    }
}