package com.reddy.finance_dashboard.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.reddy.finance_dashboard.entity.Account;
import com.reddy.finance_dashboard.entity.Stock;
import com.reddy.finance_dashboard.entity.StockPrice;
import com.reddy.finance_dashboard.entity.Transaction;
import com.reddy.finance_dashboard.entity.TransactionType;
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.repository.AccountRepository;
import com.reddy.finance_dashboard.repository.StockPriceRepository;
import com.reddy.finance_dashboard.repository.StockRepository;
import com.reddy.finance_dashboard.repository.TransactionRepository;
import com.reddy.finance_dashboard.repository.UserRepository;

import net.datafaker.Faker;

@Component
public class DataSeedingService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockPriceRepository stockPriceRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CategorizationService categorizationService;

    @Override
    @SuppressWarnings("deprecation")
    public void run(String... args) throws Exception {
        if (userRepository.count() > 4) {
            System.out.println("Database is already seeded. Skipping data generation.");
            return;
        }

        System.out.println("Database is empty. Seeding new mock data in INR...");
        Faker faker = new Faker();

        seedUsersAndTransactions(faker);
        seedStockMarket(faker);

        System.out.println("Finished seeding all mock data.");
    }

    private void seedUsersAndTransactions(Faker faker) {
        List<User> seededUsers = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(passwordEncoder.encode("password123"));
            seededUsers.add(userRepository.save(user));
        }

        for (User user : seededUsers) {
            Account account = new Account(user, new BigDecimal(faker.number().randomDouble(2, 400000, 8000000)));
            accountRepository.save(account);

            List<String> ruleTriggeringDescriptions = List.of(
                "Groceries from Reliance",
                "Weekly shopping at the grace market",
                "Flight booking via Emirates",
                "Indian Airlines ticket",
                "Dinner at Popeyes",
                "Hotel booking in ITC"
            );
            Random random = new Random();

            for (int j = 0; j < 50; j++) {
                Transaction transaction = new Transaction();
                transaction.setAccount(account);
                
                String description;
                if (j % 4 == 0) {
                    description = ruleTriggeringDescriptions.get(random.nextInt(ruleTriggeringDescriptions.size()));
                } else {
                    description = faker.commerce().productName();
                }
                transaction.setDescription(description);

                transaction.setTransactionDate(faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                
                if (faker.bool().bool()) {
                    transaction.setType(TransactionType.CREDIT);
                    transaction.setAmount(new BigDecimal(faker.number().randomDouble(2, 4000, 150000)));
                } else {
                    transaction.setType(TransactionType.DEBIT);
                    transaction.setAmount(new BigDecimal(faker.number().randomDouble(2, 800, 40000)));
                }

                categorizationService.categorizeTransaction(transaction);
                
                transactionRepository.save(transaction);
            }
        }
    }

    // v-- THIS METHOD HAS BEEN REWRITTEN --v
    private void seedStockMarket(Faker faker) {
        List<Stock> seededStocks = new ArrayList<>();
        Set<String> usedSymbols = new HashSet<>();

        for (int i = 0; i < 50; i++) {
            Stock stock = new Stock();
            String ticker;
            do {
                ticker = faker.stock().nsdqSymbol();
            } while (usedSymbols.contains(ticker));
            usedSymbols.add(ticker);
            stock.setTickerSymbol(ticker);
            stock.setCompanyName(faker.company().name());
            seededStocks.add(stockRepository.save(stock));
        }

        Random random = new Random();
        for (Stock stock : seededStocks) {
            // Start with a random price from 2 years ago
            BigDecimal currentPrice = new BigDecimal(faker.number().randomDouble(2, 50, 3000));
            LocalDateTime timestamp = LocalDateTime.now().minusYears(2);

            // Generate price points from 2 years ago until today
            while(timestamp.isBefore(LocalDateTime.now())) {
                StockPrice stockPrice = new StockPrice();
                stockPrice.setStock(stock);
                
                // Fluctuate the price by +/- 3%
                double fluctuation = (random.nextDouble() * 6) - 3;
                currentPrice = currentPrice.multiply(BigDecimal.valueOf(1 + (fluctuation / 100)));

                // Ensure price doesn't go below a minimum value (e.g., 1.00)
                if (currentPrice.compareTo(BigDecimal.ONE) < 0) {
                    currentPrice = BigDecimal.ONE;
                }
                
                stockPrice.setPrice(currentPrice.setScale(2, RoundingMode.HALF_UP));
                stockPrice.setTimestamp(timestamp);
                stockPriceRepository.save(stockPrice);
                
                // Move to the next day
                timestamp = timestamp.plusDays(1);
            }
        }
    }
}