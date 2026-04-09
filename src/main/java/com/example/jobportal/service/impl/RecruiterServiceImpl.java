package com.example.jobportal.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.jobportal.cloudinary.CloudinaryService;
import com.example.jobportal.dto.RecruiterRequest;
import com.example.jobportal.dto.ProfileResponse;
import com.example.jobportal.entity.Recruiter;
import com.example.jobportal.entity.User;
import com.example.jobportal.exception.JobException;
import com.example.jobportal.repository.RecruiterRepository;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.service.RecruiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruiterServiceImpl implements RecruiterService {

    private final UserRepository userRepository;
    private final RecruiterRepository recruiterRepository;
    private final CloudinaryService cloudinaryService;
    private final Cloudinary cloudinary;

    @Override
    public ProfileResponse updateProfile(Long userId, RecruiterRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> JobException.notFound("User not found"));

        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElse(Recruiter.builder().user(user).build());

        if (request.getCompanyEmail() != null &&
                !request.getCompanyEmail().equals(recruiter.getCompanyEmail()) &&
                recruiterRepository.existsByCompanyEmailAndUserIdNot(request.getCompanyEmail(), userId)) {

            throw JobException.badRequest("Company email already in use.");
        }

        if (request.getPanNumber() != null &&
                !request.getPanNumber().equals(recruiter.getPanNumber()) &&
                recruiterRepository.existsByPanNumberAndUserIdNot(request.getPanNumber(), userId)) {

            throw JobException.badRequest("PAN number already registered.");
        }

        if (request.getCompanyName() != null) recruiter.setCompanyName(request.getCompanyName());
        if (request.getCompanyEmail() != null) recruiter.setCompanyEmail(request.getCompanyEmail());
        if (request.getContactPerson() != null) recruiter.setContactPerson(request.getContactPerson());
        if (request.getPhoneNumber() != null) recruiter.setPhoneNumber(request.getPhoneNumber());
        if (request.getCompanyWebsite() != null) recruiter.setCompanyWebsite(request.getCompanyWebsite());
        if (request.getCompanyAddress() != null) recruiter.setCompanyAddress(request.getCompanyAddress());
        if (request.getIndustryType() != null) recruiter.setIndustryType(request.getIndustryType());
        if (request.getPanNumber() != null) recruiter.setPanNumber(request.getPanNumber());
        if (request.getDescription() != null) recruiter.setDescription(request.getDescription());

        if (request.getLogo() != null && !request.getLogo().isEmpty()) {
            try {
                if (recruiter.getPublicId() != null) {
                    cloudinary.uploader().destroy(recruiter.getPublicId(),
                            ObjectUtils.asMap("invalidate", true));
                }

                String uploadResult = cloudinaryService.uploadImage(request.getLogo());
                String[] parts = uploadResult.split("\\|");

                recruiter.setCompanyLogo(parts[0]);
                recruiter.setPublicId(parts[1]);

            } catch (Exception e) {
                throw JobException.badRequest("Failed to upload company logo. Please try again.");
            }
        }

        recruiterRepository.save(recruiter);

        return ProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role("JOB_RECRUITER")
                .companyName(recruiter.getCompanyName())
                .companyAddress(recruiter.getCompanyAddress())
                .companyEmail(recruiter.getCompanyEmail())
                .contactPerson(recruiter.getContactPerson())
                .phoneNumber(recruiter.getPhoneNumber())
                .panNumber(recruiter.getPanNumber())
                .description(recruiter.getDescription())
                .logoUrl(recruiter.getCompanyLogo())
                .publicId(recruiter.getPublicId())
                .companyWebsite(recruiter.getCompanyWebsite())
                .industryType(recruiter.getIndustryType())
                .build();
    }

    @Override
    public ProfileResponse getProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> JobException.notFound("User not found"));

        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> JobException.notFound("Recruiter profile not found"));

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
                .publicId(recruiter.getPublicId())
                .build();
    }

    @Override
    public void deleteProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> JobException.notFound("User not found"));

        Recruiter recruiter = recruiterRepository.findByUserId(user.getId())
                .orElseThrow(() -> JobException.notFound("Recruiter profile not found"));

        if (recruiter.getPublicId() != null) {
            cloudinaryService.deleteFile(recruiter.getPublicId());
        }

        recruiterRepository.delete(recruiter);
    }

    @Override
    public ProfileResponse getRecruiterProfile(Long recruiterId) {

        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> JobException.notFound("Recruiter not found"));

        return ProfileResponse.builder()
                .companyName(recruiter.getCompanyName())
                .companyEmail(recruiter.getCompanyEmail())
                .contactPerson(recruiter.getContactPerson())
                .phoneNumber(recruiter.getPhoneNumber())
                .companyWebsite(recruiter.getCompanyWebsite())
                .companyAddress(recruiter.getCompanyAddress())
                .industryType(recruiter.getIndustryType())
                .description(recruiter.getDescription())
                .logoUrl(recruiter.getCompanyLogo())
                .build();
    }
}