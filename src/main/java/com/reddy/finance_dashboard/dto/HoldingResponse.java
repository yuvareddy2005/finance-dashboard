package com.reddy.finance_dashboard.dto;

import java.math.BigDecimal;

import com.reddy.finance_dashboard.entity.Holding;
import com.reddy.finance_dashboard.entity.Stock;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response model for a single stock holding within a portfolio")
public class HoldingResponse { // <-- Now a public class

    @Schema(description = "The stock's ticker symbol", example = "AAPL")
    private String tickerSymbol;

    @Schema(description = "The company's name", example = "Apple Inc.")
    private String companyName;

    @Schema(description = "The number of shares owned", example = "10.5")
    private BigDecimal quantity;
    
    @Schema(description = "The average price at which the shares were purchased", example = "150.75")
    private BigDecimal averageBuyPrice;

    @Schema(description = "The current market price per share", example = "175.20")
    private BigDecimal currentPrice;

    @Schema(description = "The total current market value of this holding (quantity * currentPrice)", example = "1839.60")
    private BigDecimal currentValue;

    public static HoldingResponse fromEntity(Holding holding, BigDecimal currentPrice) {
        Stock stock = holding.getStock();
        BigDecimal currentValue = holding.getQuantity().multiply(currentPrice);
        return new HoldingResponse(
            stock.getTickerSymbol(),
            stock.getCompanyName(),
            holding.getQuantity(),
            holding.getAverageBuyPrice(),
            currentPrice,
            currentValue
        );
    }
}
