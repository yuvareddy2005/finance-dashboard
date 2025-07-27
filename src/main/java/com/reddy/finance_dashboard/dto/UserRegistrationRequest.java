package com.reddy.finance_dashboard.dto;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    
    private String firstName;
    private String lastName;
    private String email;
    private String password;

}