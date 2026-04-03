package com.example.jobportal.payment;

import com.example.jobportal.entity.Plan;
import com.example.jobportal.entity.RecruiterPlan;
import com.example.jobportal.enums.PaymentStatus;
import com.example.jobportal.enums.PlanType;
import com.example.jobportal.repository.PlanRepository;
import com.example.jobportal.repository.RecruiterPlanRepository;
import com.example.jobportal.service.RecruiterPlanService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@RestController
@RequestMapping("/stripe/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final RecruiterPlanService recruiterPlanService;

    private final String endpointSecret = "whsec_6a20f40bdba58b12f86ea6075c9cc950ccfa51e0f45e9825626bab495bf6795c";

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                JsonNode jsonNode = new ObjectMapper().readTree(payload);
                JsonNode sessionData = jsonNode.get("data").get("object");

                String paymentIntent = sessionData.get("payment_intent").asText();
                JsonNode metadata = sessionData.get("metadata");

                Long recruiterId = Long.parseLong(metadata.get("recruiterId").asText());
                String planName = metadata.get("planName").asText();

                recruiterPlanService.activatePlan(recruiterId, planName, paymentIntent);
                System.out.println("Recruiter plan activated for recruiterId: " + recruiterId);
            }

            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("failure");
        }
    }
}
