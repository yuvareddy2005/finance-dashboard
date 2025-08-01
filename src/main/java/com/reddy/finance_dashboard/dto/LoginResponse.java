package com.reddy.finance_dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Response model containing the JWT for an authenticated user")
public class LoginResponse {

    @Schema(description = "JSON Web Token (JWT) for the authenticated session")
    private String token;
}
