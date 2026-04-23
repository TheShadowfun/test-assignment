package com.henrikpeegel.test_assignment.controller;

import com.henrikpeegel.test_assignment.domain.AccountMissingException;
import com.henrikpeegel.test_assignment.domain.InsufficientFundsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        FieldError error = ex.getBindingResult().getFieldError();
        String message = error != null ? error.getDefaultMessage() : "Invalid input";
        return buildErrorResponse(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidEnum(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();
        if (message != null && message.contains("Currency")) {
            return buildErrorResponse("Invalid currency");
        }
        if (message != null && message.contains("Direction")) {
            return buildErrorResponse("Invalid direction");
        }
        return buildErrorResponse("Invalid input");
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientFunds(InsufficientFundsException ex) {
        return buildErrorResponse("Insufficient funds");
    }

    @ExceptionHandler(AccountMissingException.class)
    public ResponseEntity<Map<String, String>> handleAccountMissing(AccountMissingException ex) {
        return buildErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}