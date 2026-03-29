package com.example.jobportal.service.impl;

import com.example.jobportal.dto.*;
import com.example.jobportal.entity.User;
import com.example.jobportal.exception.JobException;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.security.JwtUtil;
import com.example.jobportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Response registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new JobException("User already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return new Response("User registered successfully", true);
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new JobException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new JobException("Invalid credentials");
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
        return userRepository.findAll();
    }
}