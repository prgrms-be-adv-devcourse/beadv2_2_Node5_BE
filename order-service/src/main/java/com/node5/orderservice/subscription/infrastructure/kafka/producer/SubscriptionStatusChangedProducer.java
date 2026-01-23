package com.node5.orderservice.subscription.infrastructure.kafka.producer;

import com.node5.common.event.SubscriptionStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionStatusChangedProducer {

    private final KafkaTemplate<String, SubscriptionStatusChangedEvent> kafkaTemplate;

    @Value("${kafka.topics.subscription-status-changed:order-service.subscription-status-changed.v1}")
    private String topic;

    public void send(SubscriptionStatusChangedEvent event) {
        String key = event.subscriptionId();
        kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send subscription status changed event: {}", ex.getMessage(), ex);
            }
        });
    }
}
