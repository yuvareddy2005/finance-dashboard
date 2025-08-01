package com.reddy.finance_dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request model for user registration")
public class UserRegistrationRequest {
    
    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's email address, must be unique", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "User's password", example = "a-strong-password")
    private String password;

}
