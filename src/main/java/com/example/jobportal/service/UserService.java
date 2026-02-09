package com.example.jobportal.service;

import com.example.jobportal.dto.LoginRequest;
import com.example.jobportal.dto.RegisterRequest;
import com.example.jobportal.dto.Response;

public interface UserService {
    Response registerUser(RegisterRequest request);
    Response loginUser(LoginRequest request);
}

