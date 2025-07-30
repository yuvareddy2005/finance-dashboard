package com.reddy.finance_dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reddy.finance_dashboard.entity.StockPrice;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
}
