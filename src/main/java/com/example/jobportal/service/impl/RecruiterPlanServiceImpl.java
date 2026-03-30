package com.example.jobportal.service.impl;

import com.example.jobportal.entity.Plan;
import com.example.jobportal.entity.RecruiterPlan;
import com.example.jobportal.enums.PaymentStatus;
import com.example.jobportal.enums.PlanType;
import com.example.jobportal.exception.JobException;
import com.example.jobportal.repository.PlanRepository;
import com.example.jobportal.repository.RecruiterPlanRepository;
import com.example.jobportal.service.RecruiterPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RecruiterPlanServiceImpl implements RecruiterPlanService {

    private final RecruiterPlanRepository recruiterPlanRepository;
    private final PlanRepository planRepository;

    // ---------------- ACTIVATE PLAN ----------------
    @Override
    public void activatePlan(Long recruiterId, String planName, String paymentIntent) {

        Plan plan = planRepository.findByNameAndActiveTrue(planName)
                .orElseThrow(() -> JobException.notFound("Plan not found"));

        RecruiterPlan rp = RecruiterPlan.builder()
                .recruiterId(recruiterId)
                .planType(PlanType.valueOf(plan.getName()))
                .startDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(plan.getDurationDays()))
                .jobLimit(plan.getJobLimit())
                .jobsUsed(0)
                .paymentStatus(PaymentStatus.SUCCESS)
                .stripePaymentId(paymentIntent)
                .build();

        recruiterPlanRepository.save(rp);

        System.out.println("Plan activated for recruiterId: " + recruiterId);
    }

    // ---------------- GET CURRENT PLAN ----------------
    @Override
    public RecruiterPlan getCurrentPlan(Long recruiterId) {

        return recruiterPlanRepository
                .findTopByRecruiterIdOrderByExpiryDateDesc(recruiterId)
                .orElseThrow(() -> JobException.notFound("No active subscription found"));
    }
}