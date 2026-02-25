package com.example.jobportal.payment;

import com.example.jobportal.entity.Plan;
import com.example.jobportal.repository.PlanRepository;
import org.springframework.stereotype.Service;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PlanRepository planRepository;

    public PaymentService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public PaymentResponse  createPayment(Long recruiterId, String planName) throws Exception {
        Plan plan = planRepository.findByNameAndActiveTrue(planName)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        long amount = plan.getPrice().multiply(new BigDecimal(100)).longValue();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/payment/success?recruiterId=" + recruiterId)
                .setCancelUrl("http://localhost:8080/payment/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("npr")
                                                .setUnitAmount(amount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(planName + " Package")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("recruiterId", recruiterId.toString())
                .putMetadata("planName", planName)
                .build();

        Session session = Session.create(params);

        return new PaymentResponse(
                session.getId(),
                session.getUrl(),
                plan.getPrice(),
                "NRS",
                planName
        );
    }
}
