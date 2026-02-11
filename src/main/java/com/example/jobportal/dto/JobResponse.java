package com.example.jobportal.dto;

import com.example.jobportal.enums.JobStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private BigDecimal salary;
    private JobStatus status;
    private Long recruiterId;
    private String recruiterName;
}
