package com.reddy.finance_dashboard.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}