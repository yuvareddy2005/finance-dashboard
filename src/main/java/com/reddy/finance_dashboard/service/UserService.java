package com.reddy.finance_dashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- ADD THIS IMPORT
import org.springframework.stereotype.Service;

import com.reddy.finance_dashboard.dto.UserRegistrationRequest;
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // <-- INJECT THE ENCODER

    public User createUser(UserRegistrationRequest registrationRequest) {
        User newUser = new User();
        newUser.setFirstName(registrationRequest.getFirstName());
        newUser.setLastName(registrationRequest.getLastName());
        newUser.setEmail(registrationRequest.getEmail());
        
        // v-- HASH THE PASSWORD BEFORE SETTING IT --v
        String hashedPassword = passwordEncoder.encode(registrationRequest.getPassword());
        newUser.setPassword(hashedPassword);

        return userRepository.save(newUser);
    }
}