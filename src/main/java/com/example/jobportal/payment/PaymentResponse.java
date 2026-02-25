package com.example.jobportal.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String sessionId;
    private String paymentLink;
    private BigDecimal amount;
    private String currency;
    private String planName;
}
