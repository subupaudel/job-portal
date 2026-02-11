package com.example.jobportal.service;

import com.example.jobportal.dto.ProfileRequest;
import com.example.jobportal.dto.ProfileResponse;

public interface RecruiterService {
    ProfileResponse updateProfile(Long userId, ProfileRequest request);

    ProfileResponse getProfile(Long userId);
}
