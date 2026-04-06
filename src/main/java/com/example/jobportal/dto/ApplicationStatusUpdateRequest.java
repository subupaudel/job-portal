package com.example.jobportal.dto;

import com.example.jobportal.enums.ApplicationStatus;
import lombok.Data;

import java.util.Date;

@Data
public class ApplicationStatusUpdateRequest {
    private ApplicationStatus status;
    private Date interviewDate; // required if SHORTLISTED
}
