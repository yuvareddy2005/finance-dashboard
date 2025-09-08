package com.reddy.finance_dashboard.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reddy.finance_dashboard.dto.HoldingResponse;
import com.reddy.finance_dashboard.dto.PortfolioResponse;
import com.reddy.finance_dashboard.dto.StockStatsDTO;
import com.reddy.finance_dashboard.dto.TradeRequest;
import com.reddy.finance_dashboard.entity.Account;
import com.reddy.finance_dashboard.entity.Holding;
import com.reddy.finance_dashboard.entity.Portfolio;
import com.reddy.finance_dashboard.entity.Stock;
import com.reddy.finance_dashboard.entity.StockPrice;
import com.reddy.finance_dashboard.entity.TradeOrder;
import com.reddy.finance_dashboard.entity.Transaction;
import com.reddy.finance_dashboard.entity.TransactionType;
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.repository.AccountRepository;
import com.reddy.finance_dashboard.repository.HoldingRepository;
import com.reddy.finance_dashboard.repository.PortfolioRepository;
import com.reddy.finance_dashboard.repository.StockPriceRepository;
import com.reddy.finance_dashboard.repository.StockRepository;
import com.reddy.finance_dashboard.repository.TradeOrderRepository;
import com.reddy.finance_dashboard.repository.TransactionRepository;
import com.reddy.finance_dashboard.repository.UserRepository;

import net.datafaker.Faker;

@Service
public class TradingService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockPriceRepository stockPriceRepository;
    @Autowired
    private HoldingRepository holdingRepository;
    @Autowired
    private TradeOrderRepository tradeOrderRepository;
    @Autowired
    private TransactionRepository transactionRepository; // <-- ADD THIS

    @Transactional
    public TradeOrder executeTrade(TradeRequest tradeRequest) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found for user"));
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Account not found for user"));
        Stock stock = stockRepository.findByTickerSymbol(tradeRequest.getTickerSymbol())
                .orElseThrow(() -> new IllegalStateException("Stock with symbol " + tradeRequest.getTickerSymbol() + " not found"));
        BigDecimal latestPrice = stockPriceRepository.findLatestPriceByStockId(stock.getId())
                .map(StockPrice::getPrice)
                .orElseThrow(() -> new IllegalStateException("Could not determine latest price for stock"));
        BigDecimal quantity = BigDecimal.valueOf(tradeRequest.getQuantity());
        BigDecimal totalValue = latestPrice.multiply(quantity);

        if (tradeRequest.getOrderType() == TradeOrder.OrderType.BUY) {
            if (account.getBalance().compareTo(totalValue) < 0) {
                throw new IllegalStateException("Insufficient funds to complete purchase");
            }
            account.setBalance(account.getBalance().subtract(totalValue));
            
            // Create a DEBIT transaction for the purchase
            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setAmount(totalValue);
            transaction.setType(TransactionType.DEBIT);
            transaction.setDescription("Bought " + quantity + " shares of " + stock.getTickerSymbol());
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setCategory("Trading");
            transactionRepository.save(transaction);

            Holding holding = holdingRepository.findByPortfolioAndStock(portfolio, stock)
                    .orElseGet(() -> {
                        Holding newHolding = new Holding();
                        newHolding.setPortfolio(portfolio);
                        newHolding.setStock(stock);
                        newHolding.setQuantity(BigDecimal.ZERO);
                        newHolding.setAverageBuyPrice(BigDecimal.ZERO);
                        return newHolding;
                    });
            BigDecimal newQuantity = holding.getQuantity().add(quantity);
            BigDecimal oldTotalValue = holding.getAverageBuyPrice().multiply(holding.getQuantity());
            BigDecimal newTotalValue = oldTotalValue.add(totalValue);
            holding.setAverageBuyPrice(newTotalValue.divide(newQuantity, 2, RoundingMode.HALF_UP));
            holding.setQuantity(newQuantity);
            holdingRepository.save(holding);

        } else { // SELL order
            Holding holding = holdingRepository.findByPortfolioAndStock(portfolio, stock)
                    .orElseThrow(() -> new IllegalStateException("No holdings found for this stock"));
            if (holding.getQuantity().compareTo(quantity) < 0) {
                throw new IllegalStateException("Insufficient shares to complete sale");
            }
            account.setBalance(account.getBalance().add(totalValue));

            // Create a CREDIT transaction for the sale
            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setAmount(totalValue);
            transaction.setType(TransactionType.CREDIT);
            transaction.setDescription("Sold " + quantity + " shares of " + stock.getTickerSymbol());
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setCategory("Trading");
            transactionRepository.save(transaction);
            
            holding.setQuantity(holding.getQuantity().subtract(quantity));
            holdingRepository.save(holding);
        }

        accountRepository.save(account);

        TradeOrder tradeOrder = new TradeOrder();
        tradeOrder.setPortfolio(portfolio);
        tradeOrder.setStock(stock);
        tradeOrder.setOrderType(tradeRequest.getOrderType());
        tradeOrder.setQuantity(quantity);
        tradeOrder.setPrice(latestPrice);
        tradeOrder.setTimestamp(LocalDateTime.now());
        
        return tradeOrderRepository.save(tradeOrder);
    }

    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolio() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found for user"));
        List<Holding> holdings = holdingRepository.findByPortfolio(portfolio);
        List<HoldingResponse> holdingResponses = holdings.stream()
            .map(holding -> {
                BigDecimal latestPrice = stockPriceRepository.findLatestPriceByStockId(holding.getStock().getId())
                        .map(StockPrice::getPrice)
                        .orElse(BigDecimal.ZERO);
                return HoldingResponse.fromEntity(holding, latestPrice);
            })
            .collect(Collectors.toList());
        BigDecimal totalPortfolioValue = holdingResponses.stream()
            .map(HoldingResponse::getCurrentValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PortfolioResponse(totalPortfolioValue, holdingResponses);
    }
    
    // Replace the existing getPortfolioHistory method in TradingService.java

    // Replace the existing getPortfolioHistory method in TradingService.java

    public List<com.reddy.finance_dashboard.dto.PortfolioHistoryPoint> getPortfolioHistory() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Account not found for user"));

        BigDecimal portfolioValue = getPortfolio().getTotalValue();
        BigDecimal cashBalance = account.getBalance();
        BigDecimal netWorth = portfolioValue.add(cashBalance);

        java.util.List<com.reddy.finance_dashboard.dto.PortfolioHistoryPoint> history = new java.util.ArrayList<>();
        java.util.Random random = new java.util.Random();
        BigDecimal lastValue = netWorth;

        // The user's creation date (or the start of today if null for old users)
        LocalDateTime userCreationDate = user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now().toLocalDate().atStartOfDay();

        for (int i = 30; i >= 0; i--) {
            java.time.LocalDate date = java.time.LocalDate.now().minusDays(i);
            BigDecimal valueForDay;

            // If the date is before the user was created, value is 0
            if (date.isBefore(userCreationDate.toLocalDate())) {
                valueForDay = BigDecimal.ZERO;
            } else {
                // Otherwise, calculate the simulated value
                double fluctuation = (random.nextDouble() * 4) - 2;
                lastValue = lastValue.multiply(java.math.BigDecimal.valueOf(1 + (fluctuation / 100)));
                valueForDay = lastValue;
            }
            history.add(new com.reddy.finance_dashboard.dto.PortfolioHistoryPoint(date, valueForDay.setScale(2, java.math.RoundingMode.HALF_UP)));
        }
        return history;
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }
    // Replace the old getStockPriceHistory method with this one

    public List<com.reddy.finance_dashboard.dto.PortfolioHistoryPoint> getStockPriceHistory(String tickerSymbol, String range) {
        Stock stock = stockRepository.findByTickerSymbol(tickerSymbol)
                .orElseThrow(() -> new IllegalStateException("Stock not found with symbol: " + tickerSymbol));

        LocalDateTime startDate = LocalDateTime.now();
        switch (range.toUpperCase()) {
            case "1D":
                startDate = startDate.minusDays(1);
                break;
            case "5D":
                startDate = startDate.minusDays(5);
                break;
            case "1M":
                startDate = startDate.minusMonths(1);
                break;
            case "6M":
                startDate = startDate.minusMonths(6);
                break;
            case "1Y":
                startDate = startDate.minusYears(1);
                break;
            case "5Y":
                startDate = startDate.minusYears(5);
                break;
            case "ALL":
            default:
                startDate = startDate.minusYears(100); // A long time ago to get all data
                break;
        }

        List<StockPrice> prices = stockPriceRepository.findPriceHistoryByStockIdSince(stock.getId(), startDate);

        return prices.stream()
                .map(price -> new com.reddy.finance_dashboard.dto.PortfolioHistoryPoint(
                        price.getTimestamp().toLocalDate(),
                        price.getPrice()))
                .collect(Collectors.toList());
    }
    public StockStatsDTO getStockStats(String tickerSymbol) {
        Stock stock = stockRepository.findByTickerSymbol(tickerSymbol)
                .orElseThrow(() -> new IllegalStateException("Stock not found with symbol: " + tickerSymbol));

        List<StockPrice> latestTwoPrices = stockPriceRepository.findTop2ByStockIdOrderByTimestampDesc(stock.getId());
        
        BigDecimal openPrice = stockPriceRepository.findPriceAtOrAfterTimestamp(stock.getId(), LocalDateTime.now().toLocalDate().atStartOfDay())
                .map(StockPrice::getPrice)
                .orElse(latestTwoPrices.isEmpty() ? BigDecimal.ZERO : latestTwoPrices.get(0).getPrice());

        BigDecimal prevClose = (latestTwoPrices.size() > 1) ? latestTwoPrices.get(1).getPrice() : openPrice;

        // Simulate Volume and Value
        Faker faker = new Faker();
        long volumeValue = faker.number().numberBetween(1_000_000L, 50_000_000L);
        String volume = NumberFormat.getNumberInstance(new Locale("en", "IN")).format(volumeValue);
        String totalTradedValue = NumberFormat.getNumberInstance(new Locale("en", "IN")).format(volumeValue / 1000) + " Cr";

        // Simulate Circuits
        BigDecimal currentPrice = latestTwoPrices.get(0).getPrice();
        BigDecimal upperCircuit = currentPrice.multiply(new BigDecimal("1.10")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal lowerCircuit = currentPrice.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP);


        return StockStatsDTO.builder()
                .open(openPrice)
                .prevClose(prevClose)
                .volume(volume)
                .totalTradedValue(totalTradedValue)
                .upperCircuit(upperCircuit)
                .lowerCircuit(lowerCircuit)
                .build();
    }
}