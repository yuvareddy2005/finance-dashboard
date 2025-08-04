package com.reddy.finance_dashboard.dto;

import com.reddy.finance_dashboard.entity.TradeOrder.OrderType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request model for submitting a trade order")
public class TradeRequest {

    @Schema(description = "The ticker symbol of the stock to trade", example = "AAPL")
    private String tickerSymbol;

    @Schema(description = "The number of shares to buy or sell", example = "10.5")
    private double quantity;

    @Schema(description = "The type of order", example = "BUY")
    private OrderType orderType;
}
