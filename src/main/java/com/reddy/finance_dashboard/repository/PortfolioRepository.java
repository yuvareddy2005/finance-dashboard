package com.reddy.finance_dashboard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reddy.finance_dashboard.entity.Portfolio;
import com.reddy.finance_dashboard.entity.User;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Optional<Portfolio> findByUser(User user);
}
