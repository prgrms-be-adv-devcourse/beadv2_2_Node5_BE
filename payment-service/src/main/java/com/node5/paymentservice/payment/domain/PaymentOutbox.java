package com.node5.paymentservice.payment.domain;

import com.node5.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Schema(description = "결제 아웃박스")
@Entity
@Getter
@Table(name = "\"payment_outbox\"", schema = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentOutbox extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentOutboxStatus status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "last_error_message")
    private String lastErrorMessage;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Builder
    public static PaymentOutbox ready(
            String aggregateType,
            UUID aggregateId,
            String eventType,
            String payload
    ) {
        PaymentOutbox outbox = new PaymentOutbox();
        outbox.id = UUID.randomUUID();
        outbox.aggregateType = aggregateType;
        outbox.aggregateId = aggregateId;
        outbox.eventType = eventType;
        outbox.payload = payload;
        outbox.status = PaymentOutboxStatus.READY;
        return outbox;
    }

    public void markAsSent() {
        this.status = PaymentOutboxStatus.SENT;
        this.publishedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.retryCount++;
        this.lastErrorMessage = errorMessage;
        if (this.retryCount >= 10) {
            this.status = PaymentOutboxStatus.FAILED;
            log.error("아웃박스 이벤트 최종 실패 처리 (격리됨) - ID: {}, 사유: {}", this.id, errorMessage);
        }
    }
}
