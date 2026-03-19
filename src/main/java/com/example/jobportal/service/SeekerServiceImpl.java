package com.example.jobportal.service;

import com.example.jobportal.dto.SeekerRequest;
import com.example.jobportal.dto.SeekerResponse;
import com.example.jobportal.entity.Seeker;
import com.example.jobportal.entity.User;
import com.example.jobportal.repository.SeekerRepository;
import com.example.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeekerServiceImpl implements SeekerService {

    private final SeekerRepository seekerRepository;
    private final UserRepository userRepository;

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
                .orElseThrow(() -> new RuntimeException("Seeker profile not found"));

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
}
