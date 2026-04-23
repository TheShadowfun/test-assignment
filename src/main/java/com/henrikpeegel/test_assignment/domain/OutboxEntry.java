package com.henrikpeegel.test_assignment.domain;

import lombok.Data;

@Data
public class OutboxEntry {
    private Long id;
    private String routingKey;
    private String payload;
    private String status;
}