package com.reddy.finance_dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reddy.finance_dashboard.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}