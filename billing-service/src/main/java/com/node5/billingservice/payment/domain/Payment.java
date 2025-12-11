package com.node5.billingservice.payment.domain;

import com.node5.billingservice.payment.client.dto.TossPaymentResponse;
import com.node5.billingservice.payment.exception.PaymentException;
import com.node5.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.node5.billingservice.payment.exception.PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH;
import static com.node5.billingservice.payment.exception.PaymentErrorCode.PAYMENT_KEY_MISMATCH;

@Schema(description = "결제")
@Entity
@Getter
@Table(name = "\"payment\"", schema = "billing")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

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
    private Payment(UUID walletId,
                    Long amount) {
        this.id = UUID.randomUUID();
        this.walletId = walletId;
        this.orderId = "ORDER-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        this.amount = amount;
        this.status = PaymentStatus.READY;
    }

    public void confirm(TossPaymentResponse tossPayment) {
        this.paymentKey = tossPayment.paymentKey();
        this.method = tossPayment.method();
        this.status = PaymentStatus.CONFIRMED;
        this.requestedAt = tossPayment.requestedAt().toLocalDateTime();
        this.approvedAt = tossPayment.approvedAt().toLocalDateTime();
    }

    public void failure(String failReason) {
        this.status = PaymentStatus.FAILED;
        this.failReason = failReason;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELED;
    }

    public void validateValue(Payment payment, String paymentKey, Long amount) {
        if (!payment.getPaymentKey().equals(paymentKey)) {
            throw new PaymentException(PAYMENT_KEY_MISMATCH);
        }

        if (!Objects.equals(payment.getAmount(), amount)) {
            throw new PaymentException(PAYMENT_AMOUNT_MISMATCH);
        }
    }
}
