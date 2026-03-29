package com.example.jobportal.service.impl;

import com.example.jobportal.dto.SeekerRequest;
import com.example.jobportal.dto.SeekerResponse;
import com.example.jobportal.entity.Recruiter;
import com.example.jobportal.entity.Report;
import com.example.jobportal.entity.Seeker;
import com.example.jobportal.entity.User;
import com.example.jobportal.repository.RecruiterRepository;
import com.example.jobportal.repository.ReportRepository;
import com.example.jobportal.repository.SeekerRepository;
import com.example.jobportal.repository.UserRepository;
import com.example.jobportal.service.SeekerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeekerServiceImpl implements SeekerService {

    private final SeekerRepository seekerRepository;
    private final UserRepository userRepository;
    private final RecruiterRepository recruiterRepository;
    private final ReportRepository  reportRepository;

    @Override
    public SeekerResponse createProfile(Long userId, SeekerRequest seekerRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Seeker seeker = seekerRepository.findByUserId(userId)
                .orElseGet(() -> Seeker.builder()
                        .user(userRepository.getReferenceById(userId))
                        .build());


        seeker.setFirstName(seekerRequest.getFirstName());
        seeker.setLastName(seekerRequest.getLastName());
        seeker.setPhone(seekerRequest.getContactNumber());
        seeker.setSkills(seekerRequest.getSkills());
        seeker.setExperience(seekerRequest.getExperience());
        seeker.setQualification(seekerRequest.getQualification());

        Seeker saved = seekerRepository.save(seeker);

        return SeekerResponse.builder()
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .contactNumber(saved.getPhone())
                .email(user.getEmail())
                .skills(saved.getSkills())
                .experience(saved.getExperience())
                .qualification(saved.getQualification())
                .build();
    }

    @Override
    public SeekerResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Seeker seeker = seekerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

        return SeekerResponse.builder()
                .firstName(seeker.getFirstName())
                .lastName(seeker.getLastName())
                .contactNumber(seeker.getPhone())
                .email(user.getEmail())
                .skills(seeker.getSkills())
                .experience(seeker.getExperience())
                .qualification(seeker.getQualification())
                .build();
    }

    @Override
    public void reportRecruiter(Long userId, Long recruiterId) {

        Seeker seeker = seekerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seeker not found"));

        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        // Prevent duplicate report
        if (reportRepository.existsBySeeker_IdAndRecruiter_Id(seeker.getId(), recruiterId)) {
            throw new RuntimeException("Already reported");
        }

        // Save report
        Report report = Report.builder()
                .seeker(seeker)
                .recruiter(recruiter)
                .build();

        reportRepository.save(report);

        // Count total reports
        int totalReports = reportRepository.countByRecruiter_Id(recruiterId);

        // 6️⃣ Update recruiter reportCount column
        recruiter.setReportCount(totalReports);

        // 🔥 Block recruiter if threshold reached
        int REPORT_THRESHOLD = 20;

        if (totalReports >= REPORT_THRESHOLD && !recruiter.isBlocked()) {
            recruiter.setBlocked(true);
        }
        recruiterRepository.save(recruiter);
    }

    public Long getSeekerIdByUserId(Long userId) {
        return seekerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Seeker profile not found"))
                .getId(); // seekerId
    }
}
