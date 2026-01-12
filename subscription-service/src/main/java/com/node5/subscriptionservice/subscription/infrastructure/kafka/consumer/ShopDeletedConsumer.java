package com.node5.subscriptionservice.subscription.infrastructure.kafka.consumer;

import com.node5.common.event.ShopDeletedEvent;
import com.node5.subscriptionservice.subscription.application.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopDeletedConsumer {

    private final SubscriptionService subscriptionService;

    @KafkaListener(topics = "${kafka.topics.shop-deleted:shop-service.shop-deleted.v1}")
    public void consume(ShopDeletedEvent event, Acknowledgment ack) {
        subscriptionService.terminateSellerSubscriptions(event.shopId());
        ack.acknowledge();
    }
}
