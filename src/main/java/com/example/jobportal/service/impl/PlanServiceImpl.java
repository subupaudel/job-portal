package com.example.jobportal.service.impl;

import com.example.jobportal.entity.Plan;
import com.example.jobportal.exception.JobException;
import com.example.jobportal.repository.PlanRepository;
import com.example.jobportal.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public Plan getPlanByName(String name) {
        try {
            return planRepository.findByNameAndActiveTrue(name)
                    .orElseThrow(() -> JobException.notFound("Plan not found"));
        } catch (Exception e) {
            throw JobException.badRequest("Failed to retrieve plan. Please try again.");
        }
    }
}