package com.example.jobportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RecruiterRequest {
    @NotBlank(message = "Company name is required")
    private String companyName;

    @Email(message = "Invalid email")
    private String companyEmail;

    @NotBlank(message = "Contact person required")
    private String contactPerson;

    @NotBlank(message = "Phone number required")
    private String phoneNumber;

    private String companyWebsite;
    private String companyAddress;
    private String industryType;
    private String panNumber;
    private String description;

    private MultipartFile logo;
}
