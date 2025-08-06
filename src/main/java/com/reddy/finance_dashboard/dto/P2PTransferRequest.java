package com.reddy.finance_dashboard.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request model for initiating a P2P fund transfer")
public class P2PTransferRequest {

    @Schema(description = "The email address of the recipient user", example = "jane.doe@example.com")
    private String recipientEmail;

    @Schema(description = "The amount of money to transfer", example = "500.00")
    private BigDecimal amount;
}
