package com.reddy.finance_dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reddy.finance_dashboard.dto.UserRegistrationRequest;
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.service.UserService;

@RestController
@RequestMapping("/api/v1/auth") // Base path for authentication
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register") // The new registration endpoint
    public User registerUser(@RequestBody UserRegistrationRequest registrationRequest) {
        return userService.createUser(registrationRequest);
    }
}