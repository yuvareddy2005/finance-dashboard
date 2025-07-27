package com.reddy.finance_dashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        // In the future, we will add logic here, like checking if the email already exists
        // and hashing the password. For now, we just save the user.
        return userRepository.save(user);
    }
}