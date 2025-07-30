package com.reddy.finance_dashboard.service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.reddy.finance_dashboard.entity.Account;
import com.reddy.finance_dashboard.entity.Transaction;
import com.reddy.finance_dashboard.entity.TransactionType;
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.repository.AccountRepository;
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
    private PasswordEncoder passwordEncoder;

    @Override
    @SuppressWarnings("deprecation")
    public void run(String... args) throws Exception {
        if (userRepository.count() > 4) {
            System.out.println("Database is already seeded. Skipping data generation.");
            return;
        }

        System.out.println("Database is empty. Seeding new mock data in INR...");
        Faker faker = new Faker();

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
            // v-- UPDATED FOR INR: Balance between ₹4,00,000 and ₹80,00,000 --v
            Account account = new Account(user, new BigDecimal(faker.number().randomDouble(2, 400000, 8000000)));
            accountRepository.save(account);

            for (int j = 0; j < 50; j++) {
                Transaction transaction = new Transaction();
                transaction.setAccount(account);
                transaction.setDescription(faker.commerce().productName());
                
                transaction.setTransactionDate(faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                
                if (faker.bool().bool()) {
                    transaction.setType(TransactionType.CREDIT);
                    // v-- UPDATED FOR INR: Credit between ₹4,000 and ₹1,50,000 --v
                    transaction.setAmount(new BigDecimal(faker.number().randomDouble(2, 4000, 150000)));
                } else {
                    transaction.setType(TransactionType.DEBIT);
                    // v-- UPDATED FOR INR: Debit between ₹800 and ₹40,000 --v
                    transaction.setAmount(new BigDecimal(faker.number().randomDouble(2, 800, 40000)));
                }
                transactionRepository.save(transaction);
            }
        }

        System.out.println("Finished seeding mock data in INR.");
    }
}