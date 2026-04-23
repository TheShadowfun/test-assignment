package com.henrikpeegel.test_assignment.dto;

import com.henrikpeegel.test_assignment.domain.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponseDTO {
    private BigDecimal amount;
    private Currency currency;
}
