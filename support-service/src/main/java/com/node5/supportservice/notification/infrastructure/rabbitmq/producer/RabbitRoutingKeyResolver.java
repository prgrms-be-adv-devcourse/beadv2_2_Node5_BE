package com.node5.supportservice.notification.infrastructure.rabbitmq.producer;

import com.node5.supportservice.notification.domain.NotificationChannel;
import com.node5.supportservice.notification.infrastructure.rabbitmq.config.RabbitmqConfig;
import org.springframework.stereotype.Component;

@Component
public class RabbitRoutingKeyResolver {
    public String resolve(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> RabbitmqConfig.EMAIL_ROUTING_KEY;
        };
    }
}
