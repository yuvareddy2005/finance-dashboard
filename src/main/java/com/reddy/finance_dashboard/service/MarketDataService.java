package com.reddy.finance_dashboard.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async; // <-- ADD THIS IMPORT
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

    @Async("taskExecutor") // <-- ADD THIS ANNOTATION
    @Scheduled(fixedRate = 30000)
    public void updateStockPrices() {
        // We add a small delay to simulate a longer-running task
        try {
            Thread.sleep(5000); // Sleep for 5 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("SCHEDULER: Updating stock prices on thread: " + Thread.currentThread().getName());

        List<Stock> stocks = stockRepository.findAll();

        for (Stock stock : stocks) {
            BigDecimal latestPrice = stockPriceRepository.findLatestPriceByStockId(stock.getId())
                    .map(StockPrice::getPrice)
                    .orElse(new BigDecimal("100.00"));

            double percentageChange = (random.nextDouble() * 5) - 2.5;
            BigDecimal change = latestPrice.multiply(BigDecimal.valueOf(percentageChange / 100));
            BigDecimal newPrice = latestPrice.add(change).setScale(2, RoundingMode.HALF_UP);

            if (newPrice.compareTo(BigDecimal.ONE) < 0) {
                newPrice = BigDecimal.ONE;
            }

            StockPrice newStockPrice = new StockPrice();
            newStockPrice.setStock(stock);
            newStockPrice.setPrice(newPrice);
            newStockPrice.setTimestamp(LocalDateTime.now());
            stockPriceRepository.save(newStockPrice);
        }
        System.out.println("SCHEDULER: Finished updating " + stocks.size() + " stock prices on thread: " + Thread.currentThread().getName());
    }
}
