package com.henrikpeegel.test_assignment.service;

import com.henrikpeegel.test_assignment.domain.*;
import com.henrikpeegel.test_assignment.dto.CreateAccountDTO;
import com.henrikpeegel.test_assignment.mapper.AccountMapper;
import com.henrikpeegel.test_assignment.mapper.BalanceMapper;
import com.henrikpeegel.test_assignment.mapper.OutboxMapper;
import com.henrikpeegel.test_assignment.mapper.TransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {
    private final AccountMapper accountMapper;
    private final BalanceMapper balanceMapper;
    private final TransactionMapper transactionMapper;
    private final OutboxMapper outboxMapper;
    private final ObjectMapper objectMapper;

    public AccountService(AccountMapper accountMapper, BalanceMapper balanceMapper, TransactionMapper transactionMapper, OutboxMapper outboxMapper, ObjectMapper objectMapper) {
        this.accountMapper = accountMapper;
        this.balanceMapper = balanceMapper;
        this.transactionMapper = transactionMapper;
        this.outboxMapper = outboxMapper;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Account createAccount(CreateAccountDTO dto) {
        Account account = new Account();
        account.setCustomerId(dto.getCustomerId());
        account.setCountry(dto.getCountry());

        accountMapper.insertAccount(account);

        List<Balance> balances = dto.getCurrencies().stream().map(currency -> {
            Balance b = new Balance();
            b.setAccountId(account.getId());
            b.setCurrency(currency);
            b.setAvailableAmount(BigDecimal.ZERO);
            return b;
        }).collect(Collectors.toList());

        balanceMapper.insertBalances(balances);
        account.setBalances(balances);

        try {
            OutboxEntry outboxEntry = new OutboxEntry();
            outboxEntry.setRoutingKey("test-assignment.account.created");
            outboxEntry.setPayload(objectMapper.writeValueAsString(account));
            outboxMapper.insert(outboxEntry);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize account event for outbox", e);
        }

        return account;
    }

    public Account getAccount(Long id){
        Account account = accountMapper.findById(id);
        if (account == null){
            throw new AccountMissingException("Account not found");
        }
        return account;
    }

    public List<Transaction> getTransactions(Long accountId) {
        if (accountMapper.findById(accountId) == null) {
            throw new AccountMissingException("Invalid account");
        }
        return transactionMapper.findAllByAccountId(accountId);
    }
}