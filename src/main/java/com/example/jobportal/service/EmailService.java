package com.example.jobportal.service;

import com.example.jobportal.dto.MailBody;

public interface EmailService {

    void sendOtp(String toEmail, String otp);
    void sendSimpleMessage(MailBody mailBody);
}