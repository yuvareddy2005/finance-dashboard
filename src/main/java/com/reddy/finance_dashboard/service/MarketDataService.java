package com.reddy.finance_dashboard.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.reddy.finance_dashboard.entity.Stock;
import com.reddy.finance_dashboard.entity.StockPrice;
import com.reddy.finance_dashboard.repository.StockPriceRepository;
import com.reddy.finance_dashboard.repository.StockRepository;

@Service
public class MarketDataService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockPriceRepository stockPriceRepository;

    private final Random random = new Random();

    @Async("taskExecutor")
    @Scheduled(fixedRate = 30000)
    public void updateStockPrices() {
        List<Stock> stocks = stockRepository.findAll();

        for (Stock stock : stocks) {
            BigDecimal latestPrice = stockPriceRepository.findLatestPriceByStockId(stock.getId())
                    .map(StockPrice::getPrice)
                    .orElse(new BigDecimal("100.00"));

            // Fluctuate price by a very small +/- 0.5%
            double percentageChange = (random.nextDouble() * 1.0) - 0.5;
            BigDecimal change = latestPrice.multiply(BigDecimal.valueOf(percentageChange / 100));
            BigDecimal newPrice = latestPrice.add(change);

            // v-- SAFETY CHECK TO PREVENT DRASTIC DROPS OR SPIKES --v
            BigDecimal maxAllowedPrice = latestPrice.multiply(new BigDecimal("1.03")); // Max 3% jump up
            BigDecimal minAllowedPrice = latestPrice.multiply(new BigDecimal("0.97")); // Max 3% jump down

            if (newPrice.compareTo(maxAllowedPrice) > 0) {
                newPrice = maxAllowedPrice;
            }
            if (newPrice.compareTo(minAllowedPrice) < 0) {
                newPrice = minAllowedPrice;
            }
            if (newPrice.compareTo(BigDecimal.ONE) < 0) {
                newPrice = BigDecimal.ONE;
            }

            StockPrice newStockPrice = new StockPrice();
            newStockPrice.setStock(stock);
            newStockPrice.setPrice(newPrice.setScale(2, RoundingMode.HALF_UP));
            newStockPrice.setTimestamp(LocalDateTime.now());
            stockPriceRepository.save(newStockPrice);
        }
    }
}