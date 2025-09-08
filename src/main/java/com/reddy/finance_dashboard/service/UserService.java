package com.reddy.finance_dashboard.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reddy.finance_dashboard.dto.UserRegistrationRequest;
import com.reddy.finance_dashboard.entity.Account;
import com.reddy.finance_dashboard.entity.Portfolio;
import com.reddy.finance_dashboard.entity.Transaction;
import com.reddy.finance_dashboard.entity.TransactionType;
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.repository.AccountRepository;
import com.reddy.finance_dashboard.repository.PortfolioRepository;
import com.reddy.finance_dashboard.repository.TransactionRepository;
import com.reddy.finance_dashboard.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository; // <-- Ensure this is injected

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
        newUser.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(newUser);

        // Create a portfolio for the new user
        Portfolio newPortfolio = new Portfolio(savedUser);
        portfolioRepository.save(newPortfolio);

        // v-- ALLOCATE A FIXED STARTING BALANCE --v
        Account newAccount = new Account(savedUser, new BigDecimal("100000.00"));
        accountRepository.save(newAccount);

        // v-- CREATE A TRANSACTION RECORD FOR THE INITIAL DEPOSIT --v
        Transaction initialDeposit = new Transaction();
        initialDeposit.setAccount(newAccount);
        initialDeposit.setAmount(new BigDecimal("100000.00"));
        initialDeposit.setType(TransactionType.CREDIT);
        initialDeposit.setDescription("Welcome Bonus: Initial account funding");
        initialDeposit.setTransactionDate(LocalDateTime.now());
        initialDeposit.setCategory("Platform Bonus");
        transactionRepository.save(initialDeposit);

        return savedUser;
    }
}