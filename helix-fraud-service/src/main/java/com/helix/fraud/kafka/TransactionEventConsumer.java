// Listens to the "transaction-events" Kafka topic (produced by the
// Transaction service) and hands each event to FraudDetectionService
// for rule evaluation.

package com.helix.fraud.kafka;

import com.helix.fraud.dto.TransactionEvent;
import com.helix.fraud.service.FraudDetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionEventConsumer.class);

    private final FraudDetectionService fraudDetectionService;

    @Autowired
    public TransactionEventConsumer(FraudDetectionService fraudDetectionService) {
        this.fraudDetectionService = fraudDetectionService;
    }

    @KafkaListener(topics = "transaction-events", groupId = "helix-fraud-service")
    public void consume(TransactionEvent event) {
        log.info("Received transaction event for transactionId={}", event.getTransactionId());
        fraudDetectionService.evaluate(event);
    }
}