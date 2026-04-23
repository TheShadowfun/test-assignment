package com.henrikpeegel.test_assignment.service;

import com.henrikpeegel.test_assignment.domain.Account;
import com.henrikpeegel.test_assignment.domain.Balance;
import com.henrikpeegel.test_assignment.domain.Currency;
import com.henrikpeegel.test_assignment.domain.Direction;
import com.henrikpeegel.test_assignment.domain.InsufficientFundsException;
import com.henrikpeegel.test_assignment.dto.CreateTransactionDTO;
import com.henrikpeegel.test_assignment.mapper.AccountMapper;
import com.henrikpeegel.test_assignment.mapper.BalanceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private BalanceMapper balanceMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldThrowExceptionWhenInsufficientFunds() {
        Account account = new Account();
        account.setId(1L);
        when(accountMapper.findById(any())).thenReturn(account);

        Balance balance = new Balance(1L, 1L, Currency.EUR, new BigDecimal("10.00"));
        when(balanceMapper.findBalanceForUpdate(any(), any())).thenReturn(balance);

        CreateTransactionDTO dto = new CreateTransactionDTO();
        dto.setAccountId(1L);
        dto.setCurrency(Currency.EUR);
        dto.setAmount(new BigDecimal("100.00"));
        dto.setDirection(Direction.OUT);

        assertThrows(InsufficientFundsException.class, () -> {
            transactionService.createTransaction(dto);
        });
    }
}