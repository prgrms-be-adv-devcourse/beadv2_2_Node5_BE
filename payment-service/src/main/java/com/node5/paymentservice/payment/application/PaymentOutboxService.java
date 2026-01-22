package com.node5.paymentservice.payment.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.event.PaymentDepositEvent;
import com.node5.common.event.PaymentSendEmailEvent;
import com.node5.paymentservice.payment.domain.PaymentOutbox;
import com.node5.paymentservice.payment.domain.PaymentOutboxRepository;
import com.node5.paymentservice.payment.infrastructure.kafka.producer.PaymentEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentOutboxService {
    private final PaymentOutboxRepository paymentOutboxRepository;
    private final PaymentEventProducer eventProducer;
    private final ObjectMapper objectMapper;

    @Transactional
    public List<PaymentOutbox> getBatchForUpdate() {
        return paymentOutboxRepository.findReadyForUpdate(PageRequest.of(0, 100));
    }

    // REQUIRES_NEW를 통해 스케줄러의 루프 안에서 한 건씩 개별 커밋되도록 합니다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publish(PaymentOutbox outbox) {
        try {
            String topic = outbox.getEventType();
            String key = outbox.getAggregateId().toString();
            Object payload = convertToEventObject(outbox);
            eventProducer.send(topic, key, payload);
            outbox.markAsSent();
        } catch (Exception e) {
            log.error("아웃박스 이벤트 발행 실패 - ID: {}, 사유: {}", outbox.getId(), e.getMessage());
            outbox.markAsFailed(e.getMessage());
        }
        paymentOutboxRepository.save(outbox);
    }

    private Object convertToEventObject(PaymentOutbox outbox) throws JsonProcessingException {
        String type = outbox.getAggregateType();
        String json = outbox.getPayload();
        log.info("Converting outbox ID: {}, Type: {}", outbox.getId(), type);
        // 예치금 입금 이벤트
        if (type.contains("PaymentDeposit")) {
            return objectMapper.readValue(json, PaymentDepositEvent.class);
        }
        // 결제 이메일 전송 이벤트
        if (type.contains("PaymentSendEmail")) {
            return objectMapper.readValue(json, PaymentSendEmailEvent.class);
        }

        return objectMapper.readValue(json, Map.class);
    }

    @Transactional
    public void cleanupOldMessages() {
        // 현재 시간으로부터 7일 전 시간 계산
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);

        int deletedCount = paymentOutboxRepository.deleteOldPublishedMessages(threshold);

        if (deletedCount > 0) {
            log.info("성공적으로 처리된 7일 이전 아웃박스 메시지 {}건을 삭제했습니다.", deletedCount);
        }
    }
}