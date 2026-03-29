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
    private String companyEmail;
    private String contactPerson;
    private String phoneNumber;
    private String companyWebsite;
    private String companyAddress;
    private String industryType;
    private String panNumber;
    private String description;
    private String logoUrl;
    private String publicId;
    private int reportCount;
    private boolean approved;
}
