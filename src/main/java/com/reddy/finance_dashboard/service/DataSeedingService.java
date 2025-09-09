// src/main/java/com/reddy/finance_dashboard/service/DataSeedingService.java
package com.reddy.finance_dashboard.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.reddy.finance_dashboard.entity.Account;
import com.reddy.finance_dashboard.entity.Portfolio;
import com.reddy.finance_dashboard.entity.Stock;
import com.reddy.finance_dashboard.entity.StockPrice;
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.repository.AccountRepository;
import com.reddy.finance_dashboard.repository.PortfolioRepository;
import com.reddy.finance_dashboard.repository.StockPriceRepository;
import com.reddy.finance_dashboard.repository.StockRepository;
import com.reddy.finance_dashboard.repository.UserRepository;

import net.datafaker.Faker;

@Component
public class DataSeedingService implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private StockRepository stockRepository;
    @Autowired private StockPriceRepository stockPriceRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            System.out.println("Database is already seeded. Skipping data generation.");
            return;
        }
        System.out.println("Seeding new mock data...");
        Faker faker = new Faker();
        seedUsersAndPortfolios(faker);
        seedStockMarket(faker);
        System.out.println("Finished seeding all mock data.");
    }
    
    private void seedUsersAndPortfolios(Faker faker) {
        User devUser = new User();
        devUser.setFirstName("Dev");
        devUser.setLastName("User");
        devUser.setEmail("dev@test.com");
        devUser.setPassword(passwordEncoder.encode("password"));
        devUser.setCreatedAt(LocalDateTime.now());
        userRepository.save(devUser);
        Account devAccount = new Account(devUser, new BigDecimal("2500000.00"));
        accountRepository.save(devAccount);
        portfolioRepository.save(new Portfolio(devUser));
    }

    private void seedStockMarket(Faker faker) {
        List<Stock> seededStocks = new ArrayList<>();
        Set<String> usedSymbols = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            Stock stock = new Stock();
            String ticker;
            do { ticker = faker.stock().nsdqSymbol(); } while (usedSymbols.contains(ticker));
            usedSymbols.add(ticker);
            stock.setTickerSymbol(ticker);
            stock.setCompanyName(faker.company().name());
            seededStocks.add(stockRepository.save(stock));
        }

        Random random = new Random();
        for (Stock stock : seededStocks) {
            // Generate historical daily data up to yesterday
            BigDecimal lastDailyPrice = new BigDecimal(faker.number().randomDouble(2, 50, 3000));
            LocalDateTime timestamp = LocalDateTime.now().minusYears(2);
            while (timestamp.isBefore(LocalDateTime.now().minusDays(1))) {
                double fluctuation = (random.nextDouble() * 4) - 2; // +/- 2% daily change
                lastDailyPrice = lastDailyPrice.multiply(BigDecimal.valueOf(1 + (fluctuation / 100.0)));
                if (lastDailyPrice.compareTo(BigDecimal.ONE) < 0) lastDailyPrice = BigDecimal.ONE;
                saveStockPrice(stock, lastDailyPrice, timestamp);
                timestamp = timestamp.plusDays(1);
            }

            // Generate high-frequency intraday data for the last 24 hours
            BigDecimal lastIntradayPrice = lastDailyPrice;
            timestamp = LocalDateTime.now().minusDays(1);
            while(timestamp.isBefore(LocalDateTime.now())) {
                double fluctuation = (random.nextDouble() * 0.5) - 0.25; // +/- 0.25% per minute
                lastIntradayPrice = lastIntradayPrice.multiply(BigDecimal.valueOf(1 + (fluctuation / 100.0)));
                if (lastIntradayPrice.compareTo(BigDecimal.ONE) < 0) lastIntradayPrice = BigDecimal.ONE;
                saveStockPrice(stock, lastIntradayPrice, timestamp);
                timestamp = timestamp.plusMinutes(1); // One data point per minute
            }
        }
    }
    
    private void saveStockPrice(Stock stock, BigDecimal price, LocalDateTime timestamp) {
        StockPrice sp = new StockPrice();
        sp.setStock(stock);
        sp.setPrice(price.setScale(2, RoundingMode.HALF_UP));
        sp.setTimestamp(timestamp);
        stockPriceRepository.save(sp);
    }
}