package com.henrikpeegel.test_assignment.domain;

public class AccountMissingException extends RuntimeException {
    public AccountMissingException(String message) {
        super(message);
    }
}
