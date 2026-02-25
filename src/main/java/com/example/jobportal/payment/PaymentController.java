package com.example.jobportal.payment;

import com.example.jobportal.entity.Recruiter;
import com.example.jobportal.repository.RecruiterRepository;
import com.example.jobportal.security.JwtUtil;
import com.stripe.model.PaymentIntent;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtUtil jwtUtil;
    private final RecruiterRepository  recruiterRepository;

    public PaymentController(PaymentService paymentService, JwtUtil jwtUtil,  RecruiterRepository recruiterRepository) {
        this.paymentService = paymentService;
        this.jwtUtil = jwtUtil;
        this.recruiterRepository = recruiterRepository;
    }

    @PostMapping("/create")
    public PaymentResponse  createPayment(@RequestBody PaymentRequest request,
                                       @RequestHeader("Authorization") String authHeader) throws Exception {

        String token = jwtUtil.extractToken(authHeader);
        Long userId = jwtUtil.getUserIdFromToken(token);

        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));
        Long recruiterId = recruiter.getId();

        return paymentService.createPayment(recruiterId,  request.getPlanName());
    }
}
