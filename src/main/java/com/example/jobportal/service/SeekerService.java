package com.example.jobportal.service;


import com.example.jobportal.dto.SeekerRequest;
import com.example.jobportal.dto.SeekerResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SeekerService {
    SeekerResponse createProfile(Long userId, SeekerRequest seekerRequest);

    SeekerResponse getProfile(Long userId);

    void reportRecruiter(Long userId, Long recruiterId);

    Long getSeekerIdByUserId(Long userId);
}
