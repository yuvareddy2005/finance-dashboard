package com.reddy.finance_dashboard.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reddy.finance_dashboard.dto.HoldingResponse;
import com.reddy.finance_dashboard.dto.PortfolioResponse;
import com.reddy.finance_dashboard.dto.TradeRequest;
import com.reddy.finance_dashboard.entity.Account;
import com.reddy.finance_dashboard.entity.Holding;
import com.reddy.finance_dashboard.entity.Portfolio;
import com.reddy.finance_dashboard.entity.Stock;
import com.reddy.finance_dashboard.entity.StockPrice;
import com.reddy.finance_dashboard.entity.TradeOrder;
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.repository.AccountRepository;
import com.reddy.finance_dashboard.repository.HoldingRepository;
import com.reddy.finance_dashboard.repository.PortfolioRepository;
import com.reddy.finance_dashboard.repository.StockPriceRepository;
import com.reddy.finance_dashboard.repository.StockRepository;
import com.reddy.finance_dashboard.repository.TradeOrderRepository;
import com.reddy.finance_dashboard.repository.UserRepository;

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

    @Transactional
    public TradeOrder executeTrade(TradeRequest tradeRequest) {
        // 1. Get the currently authenticated user
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // 2. Find their portfolio and account
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found for user"));
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Account not found for user"));

        // 3. Find the stock they want to trade
        Stock stock = stockRepository.findByTickerSymbol(tradeRequest.getTickerSymbol())
                .orElseThrow(() -> new IllegalStateException("Stock with symbol " + tradeRequest.getTickerSymbol() + " not found"));

        // 4. Get the latest price for that stock
        BigDecimal latestPrice = stockPriceRepository.findLatestPriceByStockId(stock.getId())
                .map(StockPrice::getPrice)
                .orElseThrow(() -> new IllegalStateException("Could not determine latest price for stock"));

        BigDecimal quantity = BigDecimal.valueOf(tradeRequest.getQuantity());
        BigDecimal totalValue = latestPrice.multiply(quantity);

        // 5. Validate and execute the trade
        if (tradeRequest.getOrderType() == TradeOrder.OrderType.BUY) {
            // Check for sufficient funds
            if (account.getBalance().compareTo(totalValue) < 0) {
                throw new IllegalStateException("Insufficient funds to complete purchase");
            }
            // 6. Update account balance
            account.setBalance(account.getBalance().subtract(totalValue));
            
            // 7. Update or create holding
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
            // Check for sufficient shares
            Holding holding = holdingRepository.findByPortfolioAndStock(portfolio, stock)
                    .orElseThrow(() -> new IllegalStateException("No holdings found for this stock"));
            if (holding.getQuantity().compareTo(quantity) < 0) {
                throw new IllegalStateException("Insufficient shares to complete sale");
            }
            // 6. Update account balance
            account.setBalance(account.getBalance().add(totalValue));
            // 7. Update holdings
            holding.setQuantity(holding.getQuantity().subtract(quantity));
            holdingRepository.save(holding);
        }

        accountRepository.save(account);

        // 8. Record the trade in the trade_orders table
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
        // 1. Get the currently authenticated user
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // 2. Find their portfolio
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found for user"));

        // 3. Find all their holdings
        List<Holding> holdings = holdingRepository.findByPortfolio(portfolio);

        // 4. Convert holdings to DTOs and calculate total value
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
}
