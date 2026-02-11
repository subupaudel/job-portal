package com.example.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Response {
    private String message;
    private boolean success;

    public Response(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}
