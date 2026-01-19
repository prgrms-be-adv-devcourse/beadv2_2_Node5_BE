package com.node5.memberservice.shop.infrastructure.kafka.producer;

import com.node5.common.event.ShopDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShopDeletedProducer {

    private final KafkaTemplate<String, ShopDeletedEvent> kafkaTemplate;

    @Value("${kafka.topics.shop-deleted:member-service.shop-deleted.v1}")
    private String topic;

    public void send(ShopDeletedEvent shopDeletedEvent) {
        String key = shopDeletedEvent.shopId().toString();
        kafkaTemplate.send(topic, key, shopDeletedEvent).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("가게 삭제 토픽 발행 실패, key={}", key, ex);
            }
        });
    }
}
