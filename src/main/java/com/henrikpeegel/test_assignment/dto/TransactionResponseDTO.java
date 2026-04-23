package com.henrikpeegel.test_assignment.dto;

import com.henrikpeegel.test_assignment.domain.Currency;
import com.henrikpeegel.test_assignment.domain.Direction;
import com.henrikpeegel.test_assignment.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransactionResponseDTO {
    private Long accountId;
    private Long transactionId;
    private BigDecimal amount;
    private Currency currency;
    private Direction direction;
    private String description;
    private BigDecimal balanceAfterTransaction;

    public static TransactionResponseDTO fromEntity(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getAccountId(),
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDirection(),
                transaction.getDescription(),
                transaction.getBalanceAfter()
        );
    }
}