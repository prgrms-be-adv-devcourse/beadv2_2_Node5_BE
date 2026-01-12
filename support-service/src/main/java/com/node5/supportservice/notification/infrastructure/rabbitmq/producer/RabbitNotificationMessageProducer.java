package com.node5.supportservice.notification.infrastructure.rabbitmq.producer;

import com.node5.supportservice.notification.application.NotificationMessageProducer;
import com.node5.supportservice.notification.domain.NotificationChannel;
import com.node5.supportservice.notification.domain.message.NotificationMessage;
import com.node5.supportservice.notification.infrastructure.rabbitmq.config.RabbitmqConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitNotificationMessageProducer implements NotificationMessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitRoutingKeyResolver routingKeyResolver;

    @Override
    public void produce(NotificationChannel channel, NotificationMessage message) {
        String routingKey = routingKeyResolver.resolve(channel);
        rabbitTemplate.convertAndSend(RabbitmqConfig.NOTIFICATION_EXCHANGE, routingKey, message);
    }

}
