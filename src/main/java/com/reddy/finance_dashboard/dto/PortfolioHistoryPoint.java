// src/main/java/com/reddy/finance_dashboard/dto/PortfolioHistoryPoint.java
package com.reddy.finance_dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PortfolioHistoryPoint {
    // Use a format that supports both date and time
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date; // Changed from 'time' to 'date' for consistency
    
    private BigDecimal value;
}