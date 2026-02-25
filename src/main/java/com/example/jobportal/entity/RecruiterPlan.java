package com.example.jobportal.entity;

import com.example.jobportal.enums.PaymentStatus;
import com.example.jobportal.enums.PlanType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "recruiter_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long recruiterId;
    @Enumerated(EnumType.STRING)
    private PlanType planType;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private Integer jobLimit;
    private Integer jobsUsed = 0;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private String stripePaymentId;
}
