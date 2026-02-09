package com.example.jobportal.service;

import com.example.jobportal.dto.LoginRequest;
import com.example.jobportal.dto.RegisterRequest;
import com.example.jobportal.entity.User;
import com.example.jobportal.enums.Role;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.jobportal.dto.Response;

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
                .role(Role.ROLE_JOBSEEKER)
                .build();

        userRepository.save(user);

        return new Response("User registered successfully", true);
    }

    @Override
    public Response loginUser(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return new Response("Invalid email or password", false);
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new Response("Invalid email or password", false);
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new Response("Login successful", true, token);
    }
}
