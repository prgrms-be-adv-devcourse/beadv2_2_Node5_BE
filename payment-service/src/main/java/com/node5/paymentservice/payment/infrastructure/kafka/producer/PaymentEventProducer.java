package com.node5.paymentservice.payment.infrastructure.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, String key, Object payload) throws Exception {
        kafkaTemplate.send(topic, key, payload)
                .get(5, java.util.concurrent.TimeUnit.SECONDS);

        log.info("Payment 이벤트 토픽 발행 성공, topic: {}, key: {}", topic, key);
    }
}
