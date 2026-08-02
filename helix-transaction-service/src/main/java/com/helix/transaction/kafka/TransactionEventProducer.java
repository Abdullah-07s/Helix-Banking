// Publishes TransactionEvent messages to the "transaction-events" Kafka
// topic. The Fraud-Detection service (Phase 6) consumes this topic to
// evaluate each completed transaction for suspicious patterns.

package com.helix.transaction.kafka;

import com.helix.transaction.dto.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventProducer {

    private static final Logger log = LoggerFactory.getLogger(TransactionEventProducer.class);
    public static final String TOPIC = "transaction-events";

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Autowired
    public TransactionEventProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(TransactionEvent event) {
        // Key by transactionId so events for the same transaction (if we
        // ever emit more than one) land on the same partition, preserving order.
        kafkaTemplate.send(TOPIC, String.valueOf(event.getTransactionId()), event);
        log.info("Published transaction event for transactionId={}", event.getTransactionId());
    }
}