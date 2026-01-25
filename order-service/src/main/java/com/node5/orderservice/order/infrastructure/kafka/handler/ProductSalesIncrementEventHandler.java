package com.node5.orderservice.order.infrastructure.kafka.handler;

import com.node5.common.event.ProductSalesIncrementEvent;
import com.node5.orderservice.order.infrastructure.kafka.producer.ProductSalesIncrementProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSalesIncrementEventHandler {
    private final ProductSalesIncrementProducer productSalesIncrementProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderItemConfirmed(ProductSalesIncrementEvent event) {
        productSalesIncrementProducer.send(event);
    }
}
