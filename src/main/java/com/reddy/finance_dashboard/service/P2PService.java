package com.reddy.finance_dashboard.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reddy.finance_dashboard.dto.P2PTransferRequest;
import com.reddy.finance_dashboard.entity.Account;
import com.reddy.finance_dashboard.entity.P2PTransfer;
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.repository.AccountRepository;
import com.reddy.finance_dashboard.repository.P2PTransferRepository;
import com.reddy.finance_dashboard.repository.UserRepository;

@Service
public class P2PService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private P2PTransferRepository p2pTransferRepository;

    @Transactional
    public P2PTransfer initiateTransfer(P2PTransferRequest transferRequest) {
        // 1. Get the currently authenticated sender from the security context
        String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalStateException("Sender not found"));

        // 2. Find the recipient user by their email
        User recipient = userRepository.findByEmail(transferRequest.getRecipientEmail())
                .orElseThrow(() -> new IllegalStateException("Recipient not found"));

        // 3. Find the accounts for both sender and recipient
        Account senderAccount = accountRepository.findByUser(sender)
                .orElseThrow(() -> new IllegalStateException("Sender account not found"));
        Account recipientAccount = accountRepository.findByUser(recipient)
                .orElseThrow(() -> new IllegalStateException("Recipient account not found"));

        BigDecimal amount = transferRequest.getAmount();

        // 4. Validate the transfer
        if (sender.getId().equals(recipient.getId())) {
            throw new IllegalStateException("Sender and recipient cannot be the same person.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Transfer amount must be positive.");
        }
        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds for transfer.");
        }

        // 5. Atomically debit the sender's account and credit the recipient's account
        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        recipientAccount.setBalance(recipientAccount.getBalance().add(amount));

        // 6. Record the transfer in the p2p_transfers table
        P2PTransfer transfer = new P2PTransfer();
        transfer.setSenderAccount(senderAccount);
        transfer.setRecipientAccount(recipientAccount);
        transfer.setAmount(amount);
        transfer.setTimestamp(LocalDateTime.now());

        return p2pTransferRepository.save(transfer);
    }
}
