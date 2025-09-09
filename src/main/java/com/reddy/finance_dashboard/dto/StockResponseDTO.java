package com.reddy.finance_dashboard.dto;

import java.math.BigDecimal;

import com.reddy.finance_dashboard.entity.Stock;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockResponseDTO {
    private String tickerSymbol;
    private String companyName;
    private BigDecimal currentPrice;

    public static StockResponseDTO fromEntity(Stock stock, BigDecimal currentPrice) {
        return new StockResponseDTO(stock.getTickerSymbol(), stock.getCompanyName(), currentPrice);
    }
}