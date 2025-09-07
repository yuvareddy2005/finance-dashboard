package com.reddy.finance_dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PortfolioHistoryPoint {
    private LocalDate date;
    private BigDecimal value;
}