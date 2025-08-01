package com.reddy.finance_dashboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Controller", description = "Endpoints for authenticated users to manage their data")
public class UserController {

    @Operation(
        summary = "Get a test message from a protected endpoint", 
        description = "This endpoint is protected and requires a valid JWT Bearer token for access."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the message"),
        @ApiResponse(responseCode = "403", description = "Forbidden - The user is not authenticated")
    })
    @SecurityRequirement(name = "bearerAuth") // This links to the security scheme we will define
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from a protected endpoint!";
    }
}
