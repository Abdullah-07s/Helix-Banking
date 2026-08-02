// Declares the RabbitMQ queue/exchange/binding used for alert
// notifications, and configures JSON message conversion.

package com.helix.fraud.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ALERTS_QUEUE = "helix.alerts.queue";
    public static final String ALERTS_EXCHANGE = "helix.alerts.exchange";
    public static final String ALERTS_ROUTING_KEY = "helix.alerts.fraud";

    @Bean
    public Queue alertsQueue() {
        return new Queue(ALERTS_QUEUE, true); // durable
    }

    @Bean
    public TopicExchange alertsExchange() {
        return new TopicExchange(ALERTS_EXCHANGE);
    }

    @Bean
    public Binding alertsBinding(Queue alertsQueue, TopicExchange alertsExchange) {
        return BindingBuilder.bind(alertsQueue).to(alertsExchange).with(ALERTS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}