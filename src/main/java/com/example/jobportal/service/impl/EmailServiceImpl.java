package com.example.jobportal.service.impl;

import com.example.jobportal.dto.MailBody;
import com.example.jobportal.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendSimpleMessage(MailBody mailBody){

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailBody.to());
        mailMessage.setFrom("your-email@gmail.com"); // ⚠️ SET THIS
        mailMessage.setSubject(mailBody.subject());
        mailMessage.setText(mailBody.text());

        mailSender.send(mailMessage);
    }
}