package com.node5.supportservice.notification.infrastructure.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    public static final String NOTIFICATION_EXCHANGE = "notification-exchange";
    public static final String EMAIL_WORKER_QUEUE = "notification.email.queue";
    public static final String EMAIL_ROUTING_KEY = "notification.email.send";

    @Bean
    public DirectExchange notificationCommandExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue emailWorkerQueue() {
        return new Queue(EMAIL_WORKER_QUEUE, true);
    }

    @Bean
    public Binding emailWorkerBinding(DirectExchange notificationCommandExchange, Queue emailWorkerQueue) {
        return BindingBuilder.bind(emailWorkerQueue).to(notificationCommandExchange).with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
