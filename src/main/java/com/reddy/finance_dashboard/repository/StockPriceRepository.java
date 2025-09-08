package com.reddy.finance_dashboard.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.reddy.finance_dashboard.entity.StockPrice;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {

    @Query("SELECT sp FROM StockPrice sp WHERE sp.stock.id = :stockId ORDER BY sp.timestamp DESC LIMIT 1")
    Optional<StockPrice> findLatestPriceByStockId(Long stockId);

    // v-- WE ARE REPLACING THE METHOD NAME WITH AN EXPLICIT QUERY --v
    @Query("SELECT sp FROM StockPrice sp WHERE sp.stock.id = :stockId ORDER BY sp.timestamp ASC")
    List<StockPrice> findPriceHistoryByStockId(Long stockId);

    @Query("SELECT sp FROM StockPrice sp WHERE sp.stock.id = :stockId AND sp.timestamp >= :startDate ORDER BY sp.timestamp ASC")
    List<StockPrice> findPriceHistoryByStockIdSince(Long stockId, LocalDateTime startDate);

    // Add these two methods inside the StockPriceRepository interface

    List<StockPrice> findTop2ByStockIdOrderByTimestampDesc(Long stockId);

    @Query("SELECT sp FROM StockPrice sp WHERE sp.stock.id = :stockId AND sp.timestamp >= :timestamp ORDER BY sp.timestamp ASC LIMIT 1")
    Optional<StockPrice> findPriceAtOrAfterTimestamp(Long stockId, LocalDateTime timestamp);
}