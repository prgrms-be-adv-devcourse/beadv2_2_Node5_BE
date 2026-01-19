package com.node5.paymentservice.payment.infrastructure.kafka.producer;

import com.node5.common.event.PaymentDepositEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentDepositEventProducer {

    private final KafkaTemplate<String, PaymentDepositEvent> kafkaTemplate;

    @Value("${kafka.topics.deposit-event:payment-service.deposit-event.v1}")
    private String topic;

    public void send(PaymentDepositEvent event) {
        String key = event.orderId();
        // Kafka로 메시지 전송 로직 구현 (예: KafkaTemplate 사용)
        kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("입금 이벤트 토픽 발행 실패, key: {}, event: {}", key, event, ex);
            } else {
                log.info("입금 이벤트 토픽 발행 성공, key: {}, event: {}", key, event);
            }
        });
    }
}
