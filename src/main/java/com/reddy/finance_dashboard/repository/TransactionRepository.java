package com.reddy.finance_dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reddy.finance_dashboard.entity.Account;
import com.reddy.finance_dashboard.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // This method name tells Spring Data JPA to generate a query that finds
    // the top 5 transactions for a given account, ordered by the transaction date
    // in descending order (most recent first).
    List<Transaction> findTop5ByAccountOrderByTransactionDateDesc(Account account);
}

