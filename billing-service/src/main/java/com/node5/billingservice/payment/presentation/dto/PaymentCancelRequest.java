package com.node5.billingservice.payment.presentation.dto;

import com.node5.billingservice.payment.application.dto.PaymentCancelCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PaymentCancelRequest(
        @NotBlank(message = "결제 키는 필수입니다.")
        String paymentKey,
        @NotBlank(message = "주문 아이디는 필수입니다.")
        String orderId,
        @Positive(message = "취소 금액은 0보다 커야 합니다.")
        Long amount
) {
    public PaymentCancelCommand toCommand() {
        return new PaymentCancelCommand(
                paymentKey,
                orderId,
                amount
        );
    }
}
