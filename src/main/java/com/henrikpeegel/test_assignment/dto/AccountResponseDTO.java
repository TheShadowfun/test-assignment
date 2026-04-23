package com.henrikpeegel.test_assignment.dto;

import com.henrikpeegel.test_assignment.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private Long accountId;
    private Long customerId;
    private List<BalanceResponseDTO> balances;

    public static AccountResponseDTO fromEntity(Account account) {
        List<BalanceResponseDTO> balanceDTOs = account.getBalances().stream()
                .map(b -> new BalanceResponseDTO(b.getAvailableAmount(), b.getCurrency()))
                .toList();

        return new AccountResponseDTO(
                account.getId(),
                account.getCustomerId(),
                balanceDTOs
        );
    }
}