package com.reddy.finance_dashboard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reddy.finance_dashboard.entity.Account;
import com.reddy.finance_dashboard.entity.User;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUser(User user);
}
