package com.example.jobportal.service;

import com.example.jobportal.dto.RecruiterRequest;
import com.example.jobportal.dto.ProfileResponse;

public interface RecruiterService {
    ProfileResponse updateProfile(Long userId, RecruiterRequest request);

    ProfileResponse getProfile(Long userId);

    void deleteProfile(Long userId);

}
