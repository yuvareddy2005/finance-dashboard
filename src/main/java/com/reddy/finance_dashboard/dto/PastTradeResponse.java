package com.reddy.finance_dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PastTradeResponse {
    private String tickerSymbol;
    private String companyName;
    private BigDecimal quantity;
    private BigDecimal averageBuyPrice;
    private BigDecimal averageSellPrice;
    private BigDecimal profitOrLoss;
    private LocalDateTime lastSellDate;
}