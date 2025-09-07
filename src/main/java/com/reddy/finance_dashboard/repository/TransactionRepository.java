package com.reddy.finance_dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- ADD THIS IMPORT

import com.reddy.finance_dashboard.entity.Account;
import com.reddy.finance_dashboard.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // v-- WE ARE REPLACING THE OLD METHOD WITH AN EXPLICIT QUERY --v
    @Query("SELECT t FROM Transaction t WHERE t.account = :account ORDER BY t.transactionDate DESC LIMIT 5")
    List<Transaction> findRecentTransactionsByAccount(Account account);
}