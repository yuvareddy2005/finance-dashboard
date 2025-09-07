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
import com.reddy.finance_dashboard.entity.Transaction; // <-- ADD THIS IMPORT
import com.reddy.finance_dashboard.entity.TransactionType; // <-- ADD THIS IMPORT
import com.reddy.finance_dashboard.entity.User;
import com.reddy.finance_dashboard.repository.AccountRepository;
import com.reddy.finance_dashboard.repository.P2PTransferRepository;
import com.reddy.finance_dashboard.repository.TransactionRepository; // <-- ADD THIS IMPORT
import com.reddy.finance_dashboard.repository.UserRepository;

@Service
public class P2PService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private P2PTransferRepository p2pTransferRepository;

    @Autowired
    private TransactionRepository transactionRepository; // <-- INJECT THE REPOSITORY

    @Transactional
    public P2PTransfer initiateTransfer(P2PTransferRequest transferRequest) {
        // 1. Get users and accounts
        String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalStateException("Sender not found"));
        User recipient = userRepository.findByEmail(transferRequest.getRecipientEmail())
                .orElseThrow(() -> new IllegalStateException("Recipient not found"));
        Account senderAccount = accountRepository.findByUser(sender)
                .orElseThrow(() -> new IllegalStateException("Sender account not found"));
        Account recipientAccount = accountRepository.findByUser(recipient)
                .orElseThrow(() -> new IllegalStateException("Recipient account not found"));
        BigDecimal amount = transferRequest.getAmount();

        // 2. Validate the transfer
        if (sender.getId().equals(recipient.getId())) {
            throw new IllegalStateException("Sender and recipient cannot be the same person.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Transfer amount must be positive.");
        }
        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds for transfer.");
        }

        // 3. Perform the transfer
        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        recipientAccount.setBalance(recipientAccount.getBalance().add(amount));

        // v-- CREATE TRANSACTION RECORDS FOR SENDER AND RECIPIENT --v
        
        // 4. Create DEBIT transaction for the sender
        Transaction senderTransaction = new Transaction();
        senderTransaction.setAccount(senderAccount);
        senderTransaction.setAmount(amount);
        senderTransaction.setType(TransactionType.DEBIT);
        senderTransaction.setDescription("Sent to " + recipient.getFirstName() + " " + recipient.getLastName());
        senderTransaction.setTransactionDate(LocalDateTime.now());
        senderTransaction.setCategory("P2P Transfer");
        transactionRepository.save(senderTransaction);

        // 5. Create CREDIT transaction for the recipient
        Transaction recipientTransaction = new Transaction();
        recipientTransaction.setAccount(recipientAccount);
        recipientTransaction.setAmount(amount);
        recipientTransaction.setType(TransactionType.CREDIT);
        recipientTransaction.setDescription("Received from " + sender.getFirstName() + " " + sender.getLastName());
        recipientTransaction.setTransactionDate(LocalDateTime.now());
        recipientTransaction.setCategory("P2P Transfer");
        transactionRepository.save(recipientTransaction);

        // 6. Record the P2P transfer itself
        P2PTransfer transfer = new P2PTransfer();
        transfer.setSenderAccount(senderAccount);
        transfer.setRecipientAccount(recipientAccount);
        transfer.setAmount(amount);
        transfer.setTimestamp(LocalDateTime.now());

        return p2pTransferRepository.save(transfer);
    }
}