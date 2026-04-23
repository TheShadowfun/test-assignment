package com.henrikpeegel.test_assignment.controller;

import com.henrikpeegel.test_assignment.domain.Account;
import com.henrikpeegel.test_assignment.dto.AccountResponseDTO;
import com.henrikpeegel.test_assignment.dto.CreateAccountDTO;
import com.henrikpeegel.test_assignment.dto.TransactionResponseDTO;
import com.henrikpeegel.test_assignment.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountResponseDTO createAccount(@Valid @RequestBody CreateAccountDTO request) {
        Account account = accountService.createAccount(request);
        return AccountResponseDTO.fromEntity(account);
    }

    @GetMapping("/{id}")
    public AccountResponseDTO getAccount(@PathVariable Long id) {
        Account account = accountService.getAccount(id);
        return AccountResponseDTO.fromEntity(account);
    }

    @GetMapping("/{id}/transactions")
    public List<TransactionResponseDTO> getTransactions(@PathVariable Long id) {
        return accountService.getTransactions(id).stream()
                .map(TransactionResponseDTO::fromEntity)
                .toList();
    }
}