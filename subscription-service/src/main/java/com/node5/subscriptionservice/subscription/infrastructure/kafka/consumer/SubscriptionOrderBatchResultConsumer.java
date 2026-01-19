package com.node5.subscriptionservice.subscription.infrastructure.kafka.consumer;

import com.node5.common.event.SubscriptionOrderBatchChunkResultEvent;
import com.node5.subscriptionservice.subscription.application.SubscriptionInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionOrderBatchResultConsumer {

    private final SubscriptionInternalService subscriptionInternalService;

    @KafkaListener(topics = "${kafka.topics.subscription-order-batch-result:batch-service.subscription-order-batch-result.v1}")
    public void consume(SubscriptionOrderBatchChunkResultEvent event, Acknowledgment ack) {
        subscriptionInternalService.applyBatchResult(event);
        ack.acknowledge();
    }
}
