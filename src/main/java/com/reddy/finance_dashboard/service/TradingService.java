package com.reddy.finance_dashboard.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reddy.finance_dashboard.dto.HoldingResponse;
import com.reddy.finance_dashboard.dto.PortfolioHistoryPoint;
import com.reddy.finance_dashboard.dto.PortfolioResponse;
import com.reddy.finance_dashboard.dto.StockResponseDTO;
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

    @Autowired private UserRepository userRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private StockRepository stockRepository;
    @Autowired private StockPriceRepository stockPriceRepository;
    @Autowired private HoldingRepository holdingRepository;
    @Autowired private TradeOrderRepository tradeOrderRepository;
    @Autowired private TransactionRepository transactionRepository;

    @Transactional
    public TradeOrder executeTrade(TradeRequest tradeRequest) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalStateException("User not found"));
        Portfolio portfolio = portfolioRepository.findByUser(user).orElseThrow(() -> new IllegalStateException("Portfolio not found for user"));
        Account account = accountRepository.findByUser(user).orElseThrow(() -> new IllegalStateException("Account not found for user"));
        Stock stock = stockRepository.findByTickerSymbol(tradeRequest.getTickerSymbol()).orElseThrow(() -> new IllegalStateException("Stock not found"));
        BigDecimal latestPrice = stockPriceRepository.findLatestPriceByStockId(stock.getId()).map(StockPrice::getPrice).orElseThrow(() -> new IllegalStateException("Could not determine latest price"));
        BigDecimal quantity = new BigDecimal(tradeRequest.getQuantity());
        BigDecimal totalValue = latestPrice.multiply(quantity);

        if (tradeRequest.getOrderType() == TradeOrder.OrderType.BUY) {
            if (account.getBalance().compareTo(totalValue) < 0) {
                throw new IllegalStateException("Insufficient funds");
            }
            account.setBalance(account.getBalance().subtract(totalValue));
            createTransaction(account, totalValue, TransactionType.DEBIT, "Bought " + quantity + " shares of " + stock.getTickerSymbol());
            updateHolding(portfolio, stock, quantity, totalValue, true);
        } else {
            Holding holding = holdingRepository.findByPortfolioAndStock(portfolio, stock).orElseThrow(() -> new IllegalStateException("No holdings found"));
            if (holding.getQuantity().compareTo(quantity) < 0) {
                throw new IllegalStateException("Insufficient shares");
            }
            account.setBalance(account.getBalance().add(totalValue));
            createTransaction(account, totalValue, TransactionType.CREDIT, "Sold " + quantity + " shares of " + stock.getTickerSymbol());
            updateHolding(portfolio, stock, quantity, totalValue, false);
        }
        accountRepository.save(account);
        return createTradeOrder(portfolio, stock, tradeRequest, latestPrice);
    }

    private void createTransaction(Account account, BigDecimal amount, TransactionType type, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDescription(description);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setCategory("Trading");
        transactionRepository.save(transaction);
    }

    private void updateHolding(Portfolio portfolio, Stock stock, BigDecimal quantity, BigDecimal totalValue, boolean isBuy) {
        Holding holding = holdingRepository.findByPortfolioAndStock(portfolio, stock).orElseGet(() -> {
            Holding newHolding = new Holding();
            newHolding.setPortfolio(portfolio);
            newHolding.setStock(stock);
            newHolding.setQuantity(BigDecimal.ZERO);
            newHolding.setAverageBuyPrice(BigDecimal.ZERO);
            return newHolding;
        });

        BigDecimal newQuantity = isBuy ? holding.getQuantity().add(quantity) : holding.getQuantity().subtract(quantity);
        if (isBuy) {
            BigDecimal oldTotalValue = holding.getAverageBuyPrice().multiply(holding.getQuantity());
            BigDecimal newTotalValue = oldTotalValue.add(totalValue);
            holding.setAverageBuyPrice(newTotalValue.divide(newQuantity, 2, RoundingMode.HALF_UP));
        }
        holding.setQuantity(newQuantity);
        holdingRepository.save(holding);
    }

    private TradeOrder createTradeOrder(Portfolio portfolio, Stock stock, TradeRequest tradeRequest, BigDecimal price) {
        TradeOrder tradeOrder = new TradeOrder();
        tradeOrder.setPortfolio(portfolio);
        tradeOrder.setStock(stock);
        tradeOrder.setOrderType(tradeRequest.getOrderType());
        tradeOrder.setQuantity(new BigDecimal(tradeRequest.getQuantity()));
        tradeOrder.setPrice(price);
        tradeOrder.setTimestamp(LocalDateTime.now());
        return tradeOrderRepository.save(tradeOrder);
    }

    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolio() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalStateException("User not found"));
        Portfolio portfolio = portfolioRepository.findByUser(user).orElseThrow(() -> new IllegalStateException("Portfolio not found"));
        List<Holding> holdings = holdingRepository.findByPortfolio(portfolio);
        List<HoldingResponse> holdingResponses = holdings.stream().map(h -> HoldingResponse.fromEntity(h, stockPriceRepository.findLatestPriceByStockId(h.getStock().getId()).map(StockPrice::getPrice).orElse(BigDecimal.ZERO))).collect(Collectors.toList());
        BigDecimal totalValue = holdingResponses.stream().map(HoldingResponse::getCurrentValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PortfolioResponse(totalValue, holdingResponses);
    }

    // THIS METHOD IS NOW CORRECTED
    public List<PortfolioHistoryPoint> getPortfolioHistory() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalStateException("User not found"));
        Account account = accountRepository.findByUser(user).orElseThrow(() -> new IllegalStateException("Account not found"));
        BigDecimal netWorth = getPortfolio().getTotalValue().add(account.getBalance());
        
        List<PortfolioHistoryPoint> history = new ArrayList<>();
        Random random = new Random();
        BigDecimal lastValue = netWorth;
        LocalDateTime userCreationDate = user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now().toLocalDate().atStartOfDay();

        for (int i = 30; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusDays(i); // <-- Use LocalDateTime
            BigDecimal valueForDay;
            if (date.toLocalDate().isBefore(userCreationDate.toLocalDate())) {
                valueForDay = BigDecimal.ZERO;
            } else {
                double fluctuation = (random.nextDouble() * 4) - 2;
                lastValue = lastValue.multiply(BigDecimal.valueOf(1 + (fluctuation / 100)));
                valueForDay = lastValue;
            }
            // The fix is here: we pass a LocalDateTime to the constructor
            history.add(new PortfolioHistoryPoint(date.toLocalDate().atStartOfDay(), valueForDay.setScale(2, RoundingMode.HALF_UP)));
        }
        return history;
    }

    public List<PortfolioHistoryPoint> getStockPriceHistory(String tickerSymbol, String range) {
        Stock stock = stockRepository.findByTickerSymbol(tickerSymbol)
                .orElseThrow(() -> new IllegalStateException("Stock not found with symbol: " + tickerSymbol));

        LocalDateTime startDate = LocalDateTime.now();
        boolean isIntraday = "1D".equalsIgnoreCase(range);

        startDate = switch (range.toUpperCase()) {
            case "1D" -> startDate.minusDays(1);
            case "1W" -> startDate.minusWeeks(1);
            case "1M" -> startDate.minusMonths(1);
            case "6M" -> startDate.minusMonths(6);
            case "1Y" -> startDate.minusYears(1);
            case "5Y" -> startDate.minusYears(5);
            default -> startDate.minusYears(100);
        };

        if (isIntraday) {
            // Fetch all high-frequency data for the "1D" view
            return stockPriceRepository.findIntradayPriceHistorySince(stock.getId(), startDate).stream()
                    .map(price -> new PortfolioHistoryPoint(
                            price.getTimestamp(),
                            price.getPrice()))
                    .collect(Collectors.toList());
        } else {
            // Fetch daily averages for all other views
            return stockPriceRepository.findDailyAveragePriceHistorySince(stock.getId(), startDate).stream()
                .map(row -> {
                    LocalDateTime date = ((java.sql.Timestamp) row[0]).toLocalDateTime();
                    BigDecimal avgPrice = new BigDecimal(row[1].toString());
                    return new PortfolioHistoryPoint(date, avgPrice);
                })
                .collect(Collectors.toList());
        }
    }

    public StockStatsDTO getStockStats(String tickerSymbol) {
        Stock stock = stockRepository.findByTickerSymbol(tickerSymbol).orElseThrow(() -> new IllegalStateException("Stock not found"));
        List<StockPrice> latestTwoPrices = stockPriceRepository.findTop2ByStockIdOrderByTimestampDesc(stock.getId());
        BigDecimal openPrice = stockPriceRepository.findPriceAtOrAfterTimestamp(stock.getId(), LocalDateTime.now().toLocalDate().atStartOfDay()).map(StockPrice::getPrice).orElse(latestTwoPrices.isEmpty() ? BigDecimal.ZERO : latestTwoPrices.get(0).getPrice());
        BigDecimal prevClose = (latestTwoPrices.size() > 1) ? latestTwoPrices.get(1).getPrice() : openPrice;
        Faker faker = new Faker();
        long volumeValue = faker.number().numberBetween(1_000_000L, 50_000_000L);
        String volume = NumberFormat.getNumberInstance(new Locale("en", "IN")).format(volumeValue);
        String totalTradedValue = NumberFormat.getNumberInstance(new Locale("en", "IN")).format(volumeValue / 1000) + " Cr";
        BigDecimal currentPrice = latestTwoPrices.get(0).getPrice();
        BigDecimal upperCircuit = currentPrice.multiply(new BigDecimal("1.10")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal lowerCircuit = currentPrice.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP);
        return StockStatsDTO.builder().open(openPrice).prevClose(prevClose).volume(volume).totalTradedValue(totalTradedValue).upperCircuit(upperCircuit).lowerCircuit(lowerCircuit).build();
    }

    public List<StockResponseDTO> getAllStocks() {
    List<Stock> stocks = stockRepository.findAll();
    return stocks.stream().map(stock -> {
        BigDecimal latestPrice = stockPriceRepository.findLatestPriceByStockId(stock.getId())
                .map(StockPrice::getPrice)
                .orElse(BigDecimal.ZERO); // Default to 0 if no price found
        return StockResponseDTO.fromEntity(stock, latestPrice);
    }).collect(Collectors.toList());
}
}