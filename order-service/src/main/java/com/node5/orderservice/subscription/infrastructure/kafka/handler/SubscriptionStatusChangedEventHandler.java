package com.node5.orderservice.subscription.infrastructure.kafka.handler;

import com.node5.common.event.SubscriptionStatusChangedEvent;
import com.node5.orderservice.subscription.infrastructure.kafka.producer.SubscriptionStatusChangedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SubscriptionStatusChangedEventHandler {

    private final SubscriptionStatusChangedProducer subscriptionStatusChangedProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(SubscriptionStatusChangedEvent event) {
        subscriptionStatusChangedProducer.send(event);
    }
}
