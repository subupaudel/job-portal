package com.example.jobportal.service.impl;

import com.example.jobportal.dto.*;
import com.example.jobportal.entity.Otp;
import com.example.jobportal.entity.User;
import com.example.jobportal.exception.JobException;
import com.example.jobportal.repository.OtpRepository;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.security.JwtUtil;
import com.example.jobportal.service.EmailService;
import com.example.jobportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private Map<String, RegisterRequest> tempUserStore = new HashMap<>();

    @Override
    public Response sendOtp(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw JobException.badRequest("Email already registered");
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        tempUserStore.put(request.getEmail(), request);

        Otp otpEntity = otpRepository.findByEmail(request.getEmail())
                .orElse(new Otp());

        otpEntity.setEmail(request.getEmail());
        otpEntity.setOtp(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otpEntity);

        emailService.sendOtp(request.getEmail(), otp);

        System.out.println("OTP: " + otp);

        return new Response("OTP sent successfully");
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> JobException.unauthorized("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw JobException.unauthorized("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }

    @Override
    public List<User> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();

            if (users.isEmpty()) {
                throw JobException.notFound("No users found");
            }

            return users;
        } catch (Exception e) {
            throw JobException.badRequest("Failed to retrieve users. Please try again.");
        }
    }

    @Override
    public Response verifyOtp(OtpRequest request) {

        Otp otpEntity = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> JobException.badRequest("OTP not found"));

        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw JobException.badRequest("OTP expired");
        }

        if (!otpEntity.getOtp().equals(request.getOtp())) {
            throw JobException.badRequest("Invalid OTP");
        }

        RegisterRequest registerRequest = tempUserStore.get(request.getEmail());

        if (registerRequest == null) {
            throw JobException.badRequest("Registration data not found");
        }

        registerUser(registerRequest);

        tempUserStore.remove(request.getEmail());
        otpRepository.delete(otpEntity);

        return new Response("User registered successfully");
    }

    private void registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw JobException.badRequest("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);
    }
}