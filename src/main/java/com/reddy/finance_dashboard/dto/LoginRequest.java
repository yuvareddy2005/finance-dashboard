package com.reddy.finance_dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request model for user login")
public class LoginRequest {

    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's password", example = "a-strong-password")
    private String password;
}
