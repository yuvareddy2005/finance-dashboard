package com.reddy.finance_dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reddy.finance_dashboard.dto.LoginRequest;
import com.reddy.finance_dashboard.dto.LoginResponse;
import com.reddy.finance_dashboard.dto.UserRegistrationRequest;
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.service.CustomUserDetailsService;
import com.reddy.finance_dashboard.service.JwtService;
import com.reddy.finance_dashboard.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication Controller", description = "Endpoints for user registration and login")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtService jwtService;

    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details.")
    @PostMapping("/register")
    public User registerUser(@RequestBody UserRegistrationRequest registrationRequest) {
        return userService.createUser(registrationRequest);
    }

    @Operation(summary = "Authenticate a user", description = "Authenticates a user with their email and password, returning a JWT upon success.")
    @PostMapping("/login")
    public LoginResponse loginUser(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token);
    }
}
