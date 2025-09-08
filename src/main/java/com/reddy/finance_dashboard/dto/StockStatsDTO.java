package com.reddy.finance_dashboard.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockStatsDTO {
    private BigDecimal open;
    private BigDecimal prevClose;
    private String volume;
    private String totalTradedValue;
    private BigDecimal upperCircuit;
    private BigDecimal lowerCircuit;
}