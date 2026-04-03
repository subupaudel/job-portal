package com.example.jobportal.service.impl;

import com.example.jobportal.dto.ProfileResponse;
import com.example.jobportal.entity.Recruiter;
import com.example.jobportal.entity.User;
import com.example.jobportal.exception.JobException;
import com.example.jobportal.repository.RecruiterRepository;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RecruiterRepository recruiterRepository;

    // ---------------- DELETE ADMIN ACCOUNT ----------------
    @Override
    public void deleteAdminAccount(Long adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> JobException.notFound("Admin not found"));

        userRepository.delete(admin);
    }

    // ---------------- RESET RECRUITER REPORT ----------------
    @Override
    public void resetRecruiterReport(Long recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> JobException.notFound("Recruiter not found"));

        recruiter.setReportCount(0);
        recruiterRepository.save(recruiter);
    }

    // ---------------- DELETE RECRUITER ----------------
    @Override
    public void deleteRecruiter(Long recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> JobException.notFound("Recruiter not found"));

        User user = recruiter.getUser();

        recruiterRepository.delete(recruiter);
        userRepository.delete(user);
    }

    // ---------------- GET REPORTED RECRUITERS ----------------
    @Override
    public List<ProfileResponse> getReportedRecruiters() {
        List<Recruiter> recruiters = recruiterRepository.findByReportCountGreaterThan(0);

        return recruiters.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ---------------- MAPPER ----------------
    private ProfileResponse mapToResponse(Recruiter recruiter) {
        return ProfileResponse.builder()
                .userId(recruiter.getUser().getId())
                .email(recruiter.getUser().getEmail())
                .role(recruiter.getUser().getRole().name())
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
                .reportCount(recruiter.getReportCount())
                .build();
    }
}