package com.reddy.finance_dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reddy.finance_dashboard.entity.Account;
import com.reddy.finance_dashboard.entity.Transaction;
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.repository.AccountRepository;
import com.reddy.finance_dashboard.repository.TransactionRepository;
import com.reddy.finance_dashboard.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transaction Controller", description = "Endpoints for managing transactions")
public class TransactionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/my-transactions")
    @Operation(summary = "Get current user's recent transactions", description = "Retrieves the 5 most recent transactions for the currently authenticated user.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Transaction>> getMyRecentTransactions() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Account not found for user"));

        // v-- CALL THE NEW, EXPLICIT QUERY METHOD --v
        List<Transaction> transactions = transactionRepository.findRecentTransactionsByAccount(account);

        return ResponseEntity.ok(transactions);
    }
}