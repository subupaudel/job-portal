package com.example.jobportal.service;

import com.example.jobportal.dto.LoginRequest;
import com.example.jobportal.dto.LoginResponse;
import com.example.jobportal.dto.RegisterRequest;
import com.example.jobportal.entity.User;
import com.example.jobportal.enums.Role;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.jobportal.dto.Response;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Response registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new Response("User already exists with email: " + request.getEmail(), false);
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
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());


        return LoginResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .userId(user.getId())
                .email(user.getEmail())
                .build();    }
}
