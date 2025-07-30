package com.reddy.finance_dashboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    // We will add endpoints here later for authenticated users,
    // for example, to get their own profile information.
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from a protected endpoint!";
    }
    
}