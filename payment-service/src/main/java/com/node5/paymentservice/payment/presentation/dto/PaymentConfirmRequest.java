package com.node5.paymentservice.payment.presentation.dto;

import com.node5.paymentservice.payment.application.dto.PaymentConfirmCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PaymentConfirmRequest(
        @NotBlank(message = "결제 키는 필수입니다.")
        String paymentKey,
        @NotBlank(message = "주문 아이디는 필수입니다.")
        String orderId,
        @Positive(message = "결제 금액은 0보다 커야 합니다.")
        Long amount
) {
    public PaymentConfirmCommand toCommand() {
        return new PaymentConfirmCommand(
                paymentKey,
                orderId,
                amount
        );
    }
}
