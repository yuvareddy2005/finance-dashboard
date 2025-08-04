package com.reddy.finance_dashboard.service;

import java.math.BigDecimal; // <-- ADD THIS IMPORT

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reddy.finance_dashboard.dto.UserRegistrationRequest;
import com.reddy.finance_dashboard.entity.Account; // <-- ADD THIS IMPORT
import com.reddy.finance_dashboard.entity.Portfolio;
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.repository.AccountRepository; // <-- ADD THIS IMPORT
import com.reddy.finance_dashboard.repository.PortfolioRepository;
import com.reddy.finance_dashboard.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AccountRepository accountRepository; // <-- INJECT THE ACCOUNT REPOSITORY

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(UserRegistrationRequest registrationRequest) {
        // Create and save the user
        User newUser = new User();
        newUser.setFirstName(registrationRequest.getFirstName());
        newUser.setLastName(registrationRequest.getLastName());
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        
        User savedUser = userRepository.save(newUser);

        // Create and save a portfolio for the new user
        Portfolio newPortfolio = new Portfolio(savedUser);
        portfolioRepository.save(newPortfolio);

        // v-- CREATE AND SAVE AN ACCOUNT FOR THE NEW USER --v
        // Real users will start with a zero balance.
        Account newAccount = new Account(savedUser, BigDecimal.ZERO);
        accountRepository.save(newAccount);

        return savedUser;
    }
}
