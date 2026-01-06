package com.node5.shopservice.shop.infrastructure.kafka.handler;

import com.node5.shopservice.shop.application.event.ShopDeletedEvent;
import com.node5.shopservice.shop.infrastructure.kafka.producer.ShopDeletedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ShopDeletedEventHandler {
    private final ShopDeletedProducer shopDeletedProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ShopDeletedEvent event) {
        shopDeletedProducer.send(event);
    }
}
