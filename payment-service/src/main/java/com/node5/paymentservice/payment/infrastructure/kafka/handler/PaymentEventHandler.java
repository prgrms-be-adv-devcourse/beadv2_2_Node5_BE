package com.node5.paymentservice.payment.infrastructure.kafka.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.paymentservice.payment.domain.PaymentOutbox;
import com.node5.paymentservice.payment.domain.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

    private final ObjectMapper objectMapper;
    private final PaymentOutboxRepository paymentOutboxRepository;

    public void saveOutbox(String type, UUID aggregateId, String eventType, Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            paymentOutboxRepository.save(
                    PaymentOutbox.ready(type, aggregateId, eventType, jsonPayload)
            );
        } catch (JsonProcessingException e) {
            log.error("Outbox serialization failed - Type: {}, ID: {}", type, aggregateId);
            throw new RuntimeException("이벤트 직렬화 실패", e);
        }
    }

}
