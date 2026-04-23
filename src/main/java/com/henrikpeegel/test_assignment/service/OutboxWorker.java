package com.henrikpeegel.test_assignment.service;

import com.henrikpeegel.test_assignment.config.RabbitMQConfig;
import com.henrikpeegel.test_assignment.domain.OutboxEntry;
import com.henrikpeegel.test_assignment.mapper.OutboxMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OutboxWorker {

    private final OutboxMapper outboxMapper;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public OutboxWorker(OutboxMapper outboxMapper, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.outboxMapper = outboxMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }


    /**
     * Asynchronous message relay worker.
     * Periodically scans the outbox table for pending events.
     * Each batch execution is wrapped in a transaction.
     * To prevent RabbitMQ Jackson converter from escaping the string,
     * it is deserialized into a JsonNode tree before transmission,
     * ensuring it arrives at the broker as native json.
     */
    @Scheduled(fixedDelayString = "${outbox.poll.interval:5000}")
    @Transactional
    public void processOutbox() {
        List<OutboxEntry> pendingEntries = outboxMapper.findPending(50);

        for (OutboxEntry entry : pendingEntries) {
            try {
                JsonNode payload = objectMapper.readTree(entry.getPayload());

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EXCHANGE_NAME,
                        entry.getRoutingKey(),
                        payload
                );

                outboxMapper.updateStatus(entry.getId(), "SENT");
                log.debug("Successfully published outbox message: {}", entry.getId());
            } catch (Exception e) {
                log.error("Failed to publish outbox entry {}: {}", entry.getId(), e.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanUpSentMessages() {
        outboxMapper.deleteOldSentMessages(LocalDateTime.now().minusDays(1));
    }
}