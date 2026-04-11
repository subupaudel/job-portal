package com.example.jobportal.service;

import com.example.jobportal.dto.*;
import com.example.jobportal.entity.User;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface UserService {
    Response sendOtp(RegisterRequest request);
    LoginResponse loginUser(LoginRequest request);
    List<User> getAllUsers();
    Response verifyOtp(OtpRequest request);
}

