package com.example.jobportal.service;

import com.example.jobportal.entity.RecruiterPlan;

public interface RecruiterPlanService {

    void activatePlan(Long recruiterId, String planName, String paymentIntent);

    RecruiterPlan getCurrentPlan(Long recruiterId);
}