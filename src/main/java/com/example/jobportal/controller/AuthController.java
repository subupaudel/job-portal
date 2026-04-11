package com.example.jobportal.controller;

import com.example.jobportal.dto.*;
import com.example.jobportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.sendOtp(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Response> verifyOtp(@RequestBody OtpRequest request) {
        return ResponseEntity.ok(userService.verifyOtp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }
}
