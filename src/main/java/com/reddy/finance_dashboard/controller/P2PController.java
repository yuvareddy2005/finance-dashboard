package com.reddy.finance_dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reddy.finance_dashboard.dto.P2PTransferRequest;
import com.reddy.finance_dashboard.entity.P2PTransfer;
import com.reddy.finance_dashboard.service.P2PService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/p2p")
@Tag(name = "P2P Transfer Controller", description = "Endpoints for peer-to-peer fund transfers")
public class P2PController {

    @Autowired
    private P2PService p2pService;

    @Operation(summary = "Initiate a fund transfer", description = "Transfers a specified amount from the authenticated user to a recipient user. Requires authentication.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/transfers")
    public ResponseEntity<P2PTransfer> initiateTransfer(@RequestBody P2PTransferRequest transferRequest) {
        P2PTransfer completedTransfer = p2pService.initiateTransfer(transferRequest);
        return ResponseEntity.ok(completedTransfer);
    }
}
