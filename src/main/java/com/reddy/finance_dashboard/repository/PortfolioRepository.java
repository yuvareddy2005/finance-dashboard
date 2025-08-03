package com.reddy.finance_dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reddy.finance_dashboard.entity.Portfolio;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
