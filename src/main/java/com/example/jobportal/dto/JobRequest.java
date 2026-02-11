package com.example.jobportal.dto;

import com.example.jobportal.enums.JobStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobRequest {
    private String title;
    private String description;
    private String location;
    private BigDecimal salary;
    private JobStatus status;
}