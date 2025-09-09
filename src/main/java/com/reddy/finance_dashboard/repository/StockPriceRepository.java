// src/main/java/com/reddy/finance_dashboard/repository/StockPriceRepository.java
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

    List<StockPrice> findTop2ByStockIdOrderByTimestampDesc(Long stockId);

    @Query("SELECT sp FROM StockPrice sp WHERE sp.stock.id = :stockId AND sp.timestamp >= :timestamp ORDER BY sp.timestamp ASC LIMIT 1")
    Optional<StockPrice> findPriceAtOrAfterTimestamp(Long stockId, LocalDateTime timestamp);

    // This query fetches all high-frequency data since a start date
    @Query("SELECT sp FROM StockPrice sp WHERE sp.stock.id = :stockId AND sp.timestamp >= :startDate ORDER BY sp.timestamp ASC")
    List<StockPrice> findIntradayPriceHistorySince(Long stockId, LocalDateTime startDate);

    // This is a native query to calculate daily average prices, which is much more efficient
    @Query(value = "SELECT " +
                   "    CAST(date_trunc('day', timestamp) AS TIMESTAMP) as timestamp, " +
                   "    AVG(price) as price " +
                   "FROM stock_prices " +
                   "WHERE stock_id = :stockId AND timestamp >= :startDate " +
                   "GROUP BY date_trunc('day', timestamp) " +
                   "ORDER BY timestamp ASC", nativeQuery = true)
    List<Object[]> findDailyAveragePriceHistorySince(Long stockId, LocalDateTime startDate);
}