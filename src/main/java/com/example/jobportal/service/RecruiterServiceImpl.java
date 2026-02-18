package com.example.jobportal.service;

import com.example.jobportal.cloudinary.CloudinaryService;
import com.example.jobportal.dto.RecruiterRequest;
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
    private final CloudinaryService  cloudinaryService;

    @Override
    public ProfileResponse updateProfile(Long userId, RecruiterRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElse(
                        Recruiter.builder()
                                .user(user)
                                .approved(false)
                                .build()
                );

        if (request.getCompanyName() != null)
            recruiter.setCompanyName(request.getCompanyName());

        if (request.getCompanyEmail() != null)
            recruiter.setCompanyEmail(request.getCompanyEmail());

        if (request.getContactPerson() != null)
            recruiter.setContactPerson(request.getContactPerson());

        if (request.getPhoneNumber() != null)
            recruiter.setPhoneNumber(request.getPhoneNumber());

        if (request.getCompanyWebsite() != null)
            recruiter.setCompanyWebsite(request.getCompanyWebsite());

        if (request.getCompanyAddress() != null)
            recruiter.setCompanyAddress(request.getCompanyAddress());

        if (request.getIndustryType() != null)
            recruiter.setIndustryType(request.getIndustryType());

        if (request.getPanNumber() != null)
            recruiter.setPanNumber(request.getPanNumber());

        if (request.getDescription() != null)
            recruiter.setDescription(request.getDescription());

        if (request.getLogo() != null && !request.getLogo().isEmpty()) {
            String logoUrl = cloudinaryService.uploadImage(request.getLogo());
            recruiter.setCompanyLogo(logoUrl);
        }

        recruiterRepository.save(recruiter);

        return ProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role("JOB_RECRUITER")
                .companyName(recruiter.getCompanyName())
                .description(recruiter.getDescription())
                .logoUrl(recruiter.getCompanyLogo())
                .approved(recruiter.isApproved())
                .build();
    }


    @Override
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        return ProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role("JOB_RECRUITER")
                .companyName(recruiter.getCompanyName())
                .companyEmail(recruiter.getCompanyEmail())
                .contactPerson(recruiter.getContactPerson())
                .phoneNumber(recruiter.getPhoneNumber())
                .companyWebsite(recruiter.getCompanyWebsite())
                .companyAddress(recruiter.getCompanyAddress())
                .industryType(recruiter.getIndustryType())
                .panNumber(recruiter.getPanNumber())
                .description(recruiter.getDescription())
                .logoUrl(recruiter.getCompanyLogo())
                .approved(recruiter.isApproved())
                .build();
    }
}
