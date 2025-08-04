package com.reddy.finance_dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response model for a user's complete stock portfolio")
public class PortfolioResponse {

    @Schema(description = "The total current market value of all holdings in the portfolio")
    private BigDecimal totalValue;

    @Schema(description = "A list of all the individual stock holdings in the portfolio")
    private List<HoldingResponse> holdings;
}