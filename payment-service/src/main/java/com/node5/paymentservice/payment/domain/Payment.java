package com.node5.paymentservice.payment.domain;

import com.node5.paymentservice.payment.application.dto.PaymentCancelCommand;
import com.node5.paymentservice.payment.client.tossPayments.dto.TossPaymentResponse;
import com.node5.paymentservice.payment.exception.PaymentException;
import com.node5.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.node5.paymentservice.payment.exception.PaymentErrorCode.*;

@Slf4j
@Schema(description = "결제")
@Entity
@Getter
@Table(name = "\"payment\"", schema = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "payment_key", unique = true, length = 200)
    private String paymentKey;

    @Column(name = "order_id", length = 100)
    private String orderId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "method", length = 50)
    private String method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "fail_reason")
    private String failReason;

    @Builder
    private Payment(UUID memberId,
                    String orderId,
                    Long amount) {
        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public void pending_confirm() {
        this.status = PaymentStatus.PENDING_CONFIRM;
    }

    public void confirm(TossPaymentResponse tossPayment) {
        this.paymentKey = tossPayment.paymentKey();
        this.method = tossPayment.method();
        this.status = PaymentStatus.CONFIRMED;
        this.requestedAt = tossPayment.requestedAt().toLocalDateTime();
        this.approvedAt = tossPayment.approvedAt().toLocalDateTime();
    }

    public void failure(String failReason) {
        this.status = PaymentStatus.PAYMENT_FAILED;
        this.failReason = failReason;
    }

    public void cancel_failure(String failReason) {
        this.status = PaymentStatus.CANCEL_FAILED;
        this.failReason = failReason;
    }

    public void pending_cancel() {
        this.status = PaymentStatus.PENDING_CANCEL;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELED;
    }

    public void validateValue(UUID memberId, PaymentCancelCommand command) {
        if (!this.memberId.equals(memberId)) {
            log.error("[Payment Error] MemberId Mismatch: expected {}, but got {}", this.memberId, memberId);
            throw new PaymentException(PAYMENT_VALIDATION_FAILED);
        }

        if (!this.paymentKey.equals(command.paymentKey())) {
            log.error("[Payment Error] PaymentKey Mismatch: expected {}, but got {}", this.paymentKey, command.paymentKey());
            throw new PaymentException(PAYMENT_VALIDATION_FAILED);
        }

        if (!Objects.equals(this.amount, command.amount())) {
            log.error("[Payment Error] Amount Mismatch: expected {}, but got {}", this.amount, command.amount());
            throw new PaymentException(PAYMENT_VALIDATION_FAILED);
        }
    }

    public void validateStatus(Payment payment, PaymentStatus paymentStatus) {
        if (payment.getStatus() != paymentStatus) {
            throw new PaymentException(PAYMENT_STATUS_INVALID);
        }
    }
}
