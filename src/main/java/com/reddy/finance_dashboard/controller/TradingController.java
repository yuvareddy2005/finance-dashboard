package com.reddy.finance_dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping; // <-- ADD THIS IMPORT
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reddy.finance_dashboard.dto.PortfolioResponse; // <-- ADD THIS IMPORT
import com.reddy.finance_dashboard.dto.TradeRequest;
import com.reddy.finance_dashboard.entity.TradeOrder;
import com.reddy.finance_dashboard.service.TradingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/trading")
@Tag(name = "Trading Controller", description = "Endpoints for executing stock trades and viewing portfolio")
public class TradingController {

    @Autowired
    private TradingService tradingService;

    @Operation(summary = "Submit a trade order", description = "Submits a buy or sell order for a given stock. Requires authentication.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/orders")
    public ResponseEntity<TradeOrder> executeTrade(@RequestBody TradeRequest tradeRequest) {
        TradeOrder executedOrder = tradingService.executeTrade(tradeRequest);
        return ResponseEntity.ok(executedOrder);
    }

    // v-- ADD THIS NEW METHOD --v
    @Operation(summary = "Get user portfolio", description = "Retrieves the current user's stock portfolio, including all holdings and their current market values. Requires authentication.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioResponse> getPortfolio() {
        PortfolioResponse portfolio = tradingService.getPortfolio();
        return ResponseEntity.ok(portfolio);
    }

    @Operation(summary = "Get user's portfolio value history", description = "Retrieves a series of data points representing the user's total portfolio value over the last 30 days. Requires authentication.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/portfolio/history")
    public ResponseEntity<java.util.List<com.reddy.finance_dashboard.dto.PortfolioHistoryPoint>> getPortfolioHistory() {
        return ResponseEntity.ok(tradingService.getPortfolioHistory());
    }
}