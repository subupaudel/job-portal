package com.example.jobportal.service;

import com.example.jobportal.dto.LoginRequest;
import com.example.jobportal.dto.LoginResponse;
import com.example.jobportal.dto.RegisterRequest;
import com.example.jobportal.dto.Response;
import com.example.jobportal.entity.User;

import java.util.List;

public interface UserService {
    Response registerUser(RegisterRequest request);
    LoginResponse loginUser(LoginRequest request);
    List<User> getAllUsers();
}

