package com.example.jobportal.service;

import com.example.jobportal.dto.ProfileRequest;
import com.example.jobportal.dto.ProfileResponse;
import com.example.jobportal.entity.Recruiter;
import com.example.jobportal.entity.User;
import com.example.jobportal.repository.RecruiterRepository;
import com.example.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruiterServiceImpl implements RecruiterService {

    private final UserRepository userRepository;
    private final RecruiterRepository recruiterRepository;


    @Override
    public ProfileResponse updateProfile(Long userId, ProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElse(
                        Recruiter.builder()
                                .user(user)
                                .approved(false)
                                .build()
                );

        recruiter.setCompanyName(request.getCompanyName());
        recruiter.setDescription(request.getDescription());
        recruiterRepository.save(recruiter);

        return ProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role("JOB_RECRUITER")
                .companyName(recruiter.getCompanyName())
                .description(recruiter.getDescription())
                .approved(recruiter.isApproved())
                .build();
    }

    @Override
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())  // or findByUser(user) if repo expects User
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        return ProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role("JOB_RECRUITER")
                .companyName(recruiter.getCompanyName())
                .description(recruiter.getDescription())
                .approved(recruiter.isApproved())
                .build();
    }
}
