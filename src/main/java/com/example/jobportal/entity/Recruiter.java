package com.example.jobportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(
        name = "recruiters"
)@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @NotBlank
    private String companyName;

    @Column(length = 1000, name = "company_email", nullable = false, unique = true)
    @NotBlank
    private String companyEmail;

    @Column(length = 1000)
    @NotBlank
    private String contactPerson;

    @Column(length = 1000)
    @NotBlank
    private String phoneNumber;

    @Column(length = 1000)
    @NotBlank
    private String companyWebsite;

    @Column(length = 1000)
    @NotBlank
    private String companyAddress;

    @Column(length = 1000)
    @NotBlank
    private String industryType;

    @Column(length = 1000, name = "pan_number", nullable = false, unique = true)
    @NotBlank
    private String panNumber;

    @Column(length = 1000)
    @NotBlank
    private String description;

    @Column(length = 1000)
    private String companyLogo;

    private String publicId;

    @Column(nullable = false)
    private boolean approved = true;
}
