package com.henrikpeegel.test_assignment.service;

import com.henrikpeegel.test_assignment.domain.*;
import com.henrikpeegel.test_assignment.dto.CreateTransactionDTO;
import com.henrikpeegel.test_assignment.dto.TransactionResponseDTO;
import com.henrikpeegel.test_assignment.mapper.AccountMapper;
import com.henrikpeegel.test_assignment.mapper.BalanceMapper;
import com.henrikpeegel.test_assignment.mapper.OutboxMapper;
import com.henrikpeegel.test_assignment.mapper.TransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final AccountMapper accountMapper;
    private final BalanceMapper balanceMapper;
    private final TransactionMapper transactionMapper;
    private final OutboxMapper outboxMapper;
    private final ObjectMapper objectMapper;

    public TransactionService(AccountMapper accountMapper, BalanceMapper balanceMapper, TransactionMapper transactionMapper, OutboxMapper outboxMapper, ObjectMapper objectMapper) {
        this.accountMapper = accountMapper;
        this.balanceMapper = balanceMapper;
        this.transactionMapper = transactionMapper;
        this.outboxMapper = outboxMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * Executes a financial transaction atomically.
     * Utilizes pessimistic row-level locking via the balanceMapper to strictly
     * serialize concurrent requests targeting the same account and currency pair.
     * Implements the Transactional Outbox pattern. The event payload is
     * staged in the local database within the same transaction boundary as the data mutation.
     * If the transaction rolls back due to an exception then the event is discarded,
     */
    @Transactional
    public TransactionResponseDTO createTransaction(CreateTransactionDTO dto) {
        if (accountMapper.findById(dto.getAccountId()) == null) {
            throw new AccountMissingException("Account missing");
        }

        Balance balance = balanceMapper.findBalanceForUpdate(dto.getAccountId(), dto.getCurrency());
        if (balance == null) {
            throw new IllegalArgumentException("Invalid currency");
        }

        BigDecimal newAmount = calculateNewBalance(balance.getAvailableAmount(), dto.getAmount(), dto.getDirection());
        balanceMapper.updateBalance(balance.getId(), newAmount);

        Transaction transaction = new Transaction();
        transaction.setAccountId(dto.getAccountId());
        transaction.setAmount(dto.getAmount());
        transaction.setCurrency(dto.getCurrency());
        transaction.setDirection(dto.getDirection());
        transaction.setDescription(dto.getDescription());
        transaction.setBalanceAfter(newAmount);

        transactionMapper.insertTransaction(transaction);
        saveToOutbox(transaction);

        return TransactionResponseDTO.fromEntity(transaction);
    }

    private BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal transactionAmount, Direction direction) {
        if (direction == Direction.OUT) {
            if (currentBalance.compareTo(transactionAmount) < 0) {
                throw new InsufficientFundsException("Insufficient funds");
            }
            return currentBalance.subtract(transactionAmount);
        } else {
            return currentBalance.add(transactionAmount);
        }
    }

    private void saveToOutbox(Transaction transaction) {
        try {
            OutboxEntry outboxEntry = new OutboxEntry();
            outboxEntry.setRoutingKey("test-assignment.transaction.created");
            outboxEntry.setPayload(objectMapper.writeValueAsString(transaction));
            outboxMapper.insert(outboxEntry);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize transaction event for outbox", e);
        }
    }
}