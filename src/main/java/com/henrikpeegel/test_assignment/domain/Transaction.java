package com.henrikpeegel.test_assignment.domain;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Transaction {
    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private Currency currency;
    private Direction direction;
    private String description;
    private BigDecimal balanceAfter;
}