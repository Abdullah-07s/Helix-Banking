// Publishes an AlertNotification message to RabbitMQ whenever a new
// fraud alert is created. Matches the required architecture: RabbitMQ
// used for alerts/notifications.

package com.helix.fraud.rabbitmq;

import com.helix.fraud.config.RabbitMQConfig;
import com.helix.fraud.dto.AlertNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlertNotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(AlertNotificationProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public AlertNotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(AlertNotification notification) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ALERTS_EXCHANGE,
                RabbitMQConfig.ALERTS_ROUTING_KEY,
                notification);
        log.info("Published alert notification for alertId={}", notification.getAlertId());
    }
}