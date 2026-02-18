package com.example.jobportal.dto;

import com.example.jobportal.enums.Qualification;
import lombok.Data;

@Data
public class SeekerRequest {

    private String firstName;
    private String lastName;
    private String contactNumber;
    private String skills;
    private String experience;
    private Qualification qualification;

}
