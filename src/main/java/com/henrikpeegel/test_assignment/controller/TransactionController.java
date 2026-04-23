package com.henrikpeegel.test_assignment.controller;

import com.henrikpeegel.test_assignment.dto.CreateTransactionDTO;
import com.henrikpeegel.test_assignment.dto.TransactionResponseDTO;
import com.henrikpeegel.test_assignment.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public TransactionResponseDTO createTransaction(@Valid @RequestBody CreateTransactionDTO request) {
        return transactionService.createTransaction(request);
    }
}