package com.example.jobportal.service;

import com.example.jobportal.entity.Plan;
import com.example.jobportal.entity.RecruiterPlan;
import com.example.jobportal.enums.PaymentStatus;
import com.example.jobportal.enums.PlanType;
import com.example.jobportal.repository.PlanRepository;
import com.example.jobportal.repository.RecruiterPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RecruiterPlanService {

    private final RecruiterPlanRepository recruiterPlanRepository;
    private final PlanRepository planRepository;

    /**
     * Activate a plan for a recruiter after successful payment.
     *
     * @param recruiterId   ID of the recruiter
     * @param planName      Name of the plan (MONTHLY, etc.)
     * @param sessionId     Stripe checkout session ID
     * @param paymentIntent Stripe PaymentIntent ID
     */
    public void activatePlan(Long recruiterId, String planName, String paymentIntent) {
        Plan plan = planRepository.findByNameAndActiveTrue(planName)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

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

        System.out.println("✅ Plan activated for recruiterId: " + recruiterId);
    }
}
