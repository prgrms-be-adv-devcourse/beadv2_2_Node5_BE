package com.node5.orderservice.order.infrastructure.kafka.producer;

import com.node5.common.event.ProductSalesIncrementEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSalesIncrementProducer {
    private final KafkaTemplate<String, ProductSalesIncrementEvent> kafkaTemplate;

    @Value("${kafka.topics.product-sales-incremented:order-service.product-sales-incremented.v1}")
    private String topic;

    public void send(ProductSalesIncrementEvent event) {
        kafkaTemplate.send(topic, event.productSalesIncrementEventId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("상품 구매확정에 따른 판매량 집계 이벤트 발행 실패: eventId={}",
                                event.productSalesIncrementEventId(), ex);
                        return;
                    }
                    String items = event.items().stream()
                            .map(item -> String.format("(orderId=%s, productId=%s, quantity=%d)", item.orderId(), item.productId(), item.quantity()))
                            .toList()
                            .toString();
                    log.info("상품 구매확정에 따른 판매량 집계 이벤트 발행 성공: eventId={}, items={}",
                            event.productSalesIncrementEventId(), items);
                });
    }
}
