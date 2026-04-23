package com.henrikpeegel.test_assignment;

import com.henrikpeegel.test_assignment.domain.Currency;
import com.henrikpeegel.test_assignment.domain.Direction;
import com.henrikpeegel.test_assignment.dto.CreateAccountDTO;
import com.henrikpeegel.test_assignment.dto.CreateTransactionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class IntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.12-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void fullAccountAndTransactionLifecycle() {
        // 1. Create Account
        CreateAccountDTO accountDTO = new CreateAccountDTO();
        accountDTO.setCustomerId(1L);
        accountDTO.setCountry("EE");
        accountDTO.setCurrencies(List.of(Currency.EUR, Currency.USD));

        ResponseEntity<Map> accountResponse = restTemplate.postForEntity("/accounts", accountDTO, Map.class);
        assertEquals(HttpStatus.OK, accountResponse.getStatusCode());

        Integer accountId = (Integer) accountResponse.getBody().get("accountId");
        assertNotNull(accountId);

        // 2. Deposit Funds (IN)
        CreateTransactionDTO depositDTO = new CreateTransactionDTO();
        depositDTO.setAccountId(accountId.longValue());
        depositDTO.setAmount(new BigDecimal("100.00"));
        depositDTO.setCurrency(Currency.EUR);
        depositDTO.setDirection(Direction.IN);
        depositDTO.setDescription("Initial Deposit");

        ResponseEntity<Map> depositResponse = restTemplate.postForEntity("/transactions", depositDTO, Map.class);
        assertEquals(HttpStatus.OK, depositResponse.getStatusCode());

        // 3. Attempt Invalid Withdrawal (OUT) - Insufficient Funds
        CreateTransactionDTO withdrawalDTO = new CreateTransactionDTO();
        withdrawalDTO.setAccountId(accountId.longValue());
        withdrawalDTO.setAmount(new BigDecimal("200.00"));
        withdrawalDTO.setCurrency(Currency.EUR);
        withdrawalDTO.setDirection(Direction.OUT);
        withdrawalDTO.setDescription("Oversized Withdrawal");

        ResponseEntity<Map> withdrawalResponse = restTemplate.postForEntity("/transactions", withdrawalDTO, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, withdrawalResponse.getStatusCode());
        assertEquals("Insufficient funds", withdrawalResponse.getBody().get("error"));
    }
}