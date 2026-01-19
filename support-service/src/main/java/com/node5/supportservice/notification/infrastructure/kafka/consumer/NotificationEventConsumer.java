package com.node5.supportservice.notification.infrastructure.kafka.consumer;

import com.node5.common.event.OrderStatusChangedEvent;
import com.node5.common.event.SubscriptionStatusChangedEvent;
import com.node5.supportservice.notification.application.NotificationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationHandler notificationHandler;

    @KafkaListener(topics = "${kafka.topics.order-status-changed:order-service.order-status-changed.v1}")
    public void orderStatusChangedConsume(OrderStatusChangedEvent event, Acknowledgment ack) {
        notificationHandler.orderStatusChangedHandle(event);
        ack.acknowledge();
    }

    @KafkaListener(topics = "${kafka.topics.subscription-status-changed:subscription-service.subscription-status-changed.v1}")
    public void subscriptionStatusChangedConsume(SubscriptionStatusChangedEvent event, Acknowledgment ack) {
        notificationHandler.subscriptionStatusChangedHandle(event);
        ack.acknowledge();
    }
}
