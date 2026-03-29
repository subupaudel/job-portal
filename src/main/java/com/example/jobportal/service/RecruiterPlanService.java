package com.example.jobportal.service;

import com.example.jobportal.entity.RecruiterPlan;

public interface RecruiterPlanService {

    // Activate subscription plan
    void activatePlan(Long recruiterId, String planName, String paymentIntent);

    // Get current active plan
    RecruiterPlan getCurrentPlan(Long recruiterId);
}