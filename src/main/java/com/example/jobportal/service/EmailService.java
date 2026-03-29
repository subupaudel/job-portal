package com.example.jobportal.service;

import com.example.jobportal.dto.MailBody;

public interface EmailService {

    void sendSimpleMessage(MailBody mailBody);
}