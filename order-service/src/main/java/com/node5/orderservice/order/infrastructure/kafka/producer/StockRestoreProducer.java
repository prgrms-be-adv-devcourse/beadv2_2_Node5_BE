package com.node5.orderservice.order.infrastructure.kafka.producer;

import com.node5.common.event.StockRestoreEvent;
import com.node5.orderservice.order.infrastructure.kafka.dto.StockRestoreKafkaRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockRestoreProducer {
    private final KafkaTemplate<String, StockRestoreEvent> kafkaTemplate;

    @Value("${kafka.topics.stock-restore:order-service.stock-restore.v1}")
    private String topic;

    public void send(StockRestoreKafkaRequest request) {
        StockRestoreEvent event = request.toEvent();
        kafkaTemplate.send(topic, event.orderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[{}]에 대한 재고 복구 이벤트 발행 실패: orderId={}", request.type(), event.orderId(), ex);
                    }
                });
    }
}
