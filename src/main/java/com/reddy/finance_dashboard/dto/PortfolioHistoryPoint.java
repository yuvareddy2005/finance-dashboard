package com.reddy.finance_dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat; // <-- ADD THIS IMPORT

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PortfolioHistoryPoint {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // <-- ADD THIS ANNOTATION
    private LocalDate date;
    
    private BigDecimal value;
}