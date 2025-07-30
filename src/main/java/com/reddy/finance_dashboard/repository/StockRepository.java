package com.reddy.finance_dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reddy.finance_dashboard.entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
