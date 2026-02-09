package com.example.jobportal.controller;

import com.example.jobportal.dto.LoginRequest;
import com.example.jobportal.dto.RegisterRequest;
import com.example.jobportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobportal.dto.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody RegisterRequest registrationRequest) {
        return ResponseEntity.ok(userService.registerUser(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Response> loginUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }
}
