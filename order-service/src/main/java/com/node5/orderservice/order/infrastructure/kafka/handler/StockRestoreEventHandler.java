package com.node5.orderservice.order.infrastructure.kafka.handler;

import com.node5.orderservice.order.infrastructure.kafka.producer.StockRestoreProducer;
import com.node5.orderservice.order.infrastructure.kafka.dto.StockRestoreKafkaRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockRestoreEventHandler {
    private final StockRestoreProducer stockRestoreProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStockRestore(StockRestoreKafkaRequest request) {
        stockRestoreProducer.send(request);
    }

}
