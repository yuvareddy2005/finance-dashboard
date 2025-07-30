package com.reddy.finance_dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reddy.finance_dashboard.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
