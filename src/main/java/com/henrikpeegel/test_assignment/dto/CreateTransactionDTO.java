package com.henrikpeegel.test_assignment.dto;

import com.henrikpeegel.test_assignment.domain.Currency;
import com.henrikpeegel.test_assignment.domain.Direction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransactionDTO {
    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Invalid amount")
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    private Currency currency;

    @NotNull(message = "Direction is required")
    private Direction direction;

    @NotBlank(message = "Description is required")
    private String description;
}
