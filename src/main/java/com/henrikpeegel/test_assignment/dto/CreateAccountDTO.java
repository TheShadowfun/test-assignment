package com.henrikpeegel.test_assignment.dto;

import com.henrikpeegel.test_assignment.domain.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateAccountDTO {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Country is required")
    private String country;

    @NotEmpty(message = "At least one currency must be provided")
    private List<Currency> currencies;
}
