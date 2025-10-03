package com.adminapplicationmaster.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String contact;
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}