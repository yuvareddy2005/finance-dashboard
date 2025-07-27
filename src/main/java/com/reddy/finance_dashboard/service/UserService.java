package com.reddy.finance_dashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reddy.finance_dashboard.dto.UserRegistrationRequest;
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(UserRegistrationRequest registrationRequest) {
        // This is the mapping step from DTO to Entity
        User newUser = new User();
        newUser.setFirstName(registrationRequest.getFirstName());
        newUser.setLastName(registrationRequest.getLastName());
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setPassword(registrationRequest.getPassword()); // We will hash this later

        return userRepository.save(newUser);
    }
}