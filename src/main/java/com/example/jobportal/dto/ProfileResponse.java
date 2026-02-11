package com.example.jobportal.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {

    private Long userId;
    private String email;
    private String role;

    private String companyName;
    private String description;
    private Boolean approved;
}
