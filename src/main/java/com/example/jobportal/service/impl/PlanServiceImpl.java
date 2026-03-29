package com.example.jobportal.service.impl;

import com.example.jobportal.entity.Plan;
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
        return planRepository.findByNameAndActiveTrue(name)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
    }
}