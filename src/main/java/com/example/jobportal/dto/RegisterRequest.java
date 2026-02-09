package com.example.jobportal.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.example.jobportal.enums.Role;

@Data
@Getter
@Setter
public class RegisterRequest {
    private  String name;
    private String email;
    private String password;
    private Role role;
}
