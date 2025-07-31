package com.reddy.finance_dashboard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.reddy.finance_dashboard.entity.StockPrice;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {

    @Query("SELECT sp FROM StockPrice sp WHERE sp.stock.id = :stockId ORDER BY sp.timestamp DESC LIMIT 1")
    Optional<StockPrice> findLatestPriceByStockId(Long stockId);
}
