package com.example.jobportal.service;

import com.example.jobportal.dto.LoginRequest;
import com.example.jobportal.dto.LoginResponse;
import com.example.jobportal.dto.RegisterRequest;
import com.example.jobportal.dto.Response;

public interface UserService {
    Response registerUser(RegisterRequest request);
    LoginResponse loginUser(LoginRequest request);
}

