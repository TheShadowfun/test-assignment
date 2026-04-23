package com.henrikpeegel.test_assignment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Balance {
    private Long id;
    private Long accountId;
    private Currency currency;
    private BigDecimal availableAmount;
}