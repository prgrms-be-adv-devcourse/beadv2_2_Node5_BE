package com.node5.billingservice.payment.domain;

import com.node5.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "결제")
@Entity
@Getter
@Table(name = "\"payment_failure\"", schema = "billing")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentFailure extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "order_id", nullable = false, length = 100)
    private String orderId;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "amount")
    private Long amount;

    @Builder
    private PaymentFailure(UUID walletId,
                           String orderId,
                           String errorCode,
                           String errorMessage,
                           Long amount) {
        this.id = UUID.randomUUID();
        this.walletId = walletId;
        this.orderId = orderId;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.amount = amount;
    }
}
