package com.example.jobportal.service;

import com.example.jobportal.entity.Plan;
import com.example.jobportal.repository.PlanRepository;
import org.springframework.stereotype.Service;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public Plan getPlanByName(String name) {
        return planRepository.findByNameAndActiveTrue(name)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
    }
}
