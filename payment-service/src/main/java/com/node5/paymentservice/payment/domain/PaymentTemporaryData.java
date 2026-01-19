package com.node5.paymentservice.payment.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class PaymentTemporaryData {
    private UUID memberId;
    private String orderId;
    private Long amount;
    private LocalDateTime createdAt;

    public PaymentTemporaryData(UUID memberId, String orderId, Long amount) {
        this.memberId = memberId;
        this.orderId = orderId;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }
}
