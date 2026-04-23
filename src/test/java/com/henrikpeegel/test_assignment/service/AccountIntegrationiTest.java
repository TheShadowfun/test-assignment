package com.henrikpeegel.test_assignment.service;

import com.henrikpeegel.test_assignment.domain.Account;
import com.henrikpeegel.test_assignment.domain.Currency;
import com.henrikpeegel.test_assignment.domain.OutboxEntry;
import com.henrikpeegel.test_assignment.dto.CreateAccountDTO;
import com.henrikpeegel.test_assignment.mapper.AccountMapper;
import com.henrikpeegel.test_assignment.mapper.OutboxMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class AccountIntegrationTest {

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
    private AccountService accountService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private OutboxMapper outboxMapper;

    @Test
    void shouldCreateAccountPersistBalancesAndStageOutboxEvent() {
        CreateAccountDTO dto = new CreateAccountDTO();
        dto.setCustomerId(999L);
        dto.setCountry("EE");
        dto.setCurrencies(List.of(Currency.EUR, Currency.USD));

        Account createdAccount = accountService.createAccount(dto);

        assertNotNull(createdAccount.getId());
        assertEquals(2, createdAccount.getBalances().size());
        assertEquals(BigDecimal.ZERO, createdAccount.getBalances().get(0).getAvailableAmount());

        Account persistedAccount = accountMapper.findById(createdAccount.getId());
        assertNotNull(persistedAccount);
        assertEquals(999L, persistedAccount.getCustomerId());
        assertEquals(2, persistedAccount.getBalances().size());

        List<OutboxEntry> pendingEvents = outboxMapper.findPending(10);
        assertTrue(pendingEvents.stream()
                .anyMatch(event -> event.getRoutingKey().equals("account.created") &&
                        event.getPayload().contains("\"customerId\":999")));
    }
}