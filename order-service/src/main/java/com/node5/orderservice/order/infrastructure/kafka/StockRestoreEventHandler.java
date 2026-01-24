package com.node5.orderservice.order.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockRestoreEventHandler {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.stock-restore:order-service.stock-restore.v1}")
    private String topic;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStockRestore(StockRestoreEvent event) {
        StockRestoreKafkaRequest kafkaPayload = StockRestoreKafkaRequest.create(event);

        kafkaTemplate.send(topic, event.orderId().toString(), kafkaPayload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[{}]에 대한 재고 복구 이벤트 발행 실패: orderId={}", event.type(), event.orderId(), ex);
                    }
                });
    }
}
