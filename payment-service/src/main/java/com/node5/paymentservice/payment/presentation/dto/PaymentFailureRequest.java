package com.node5.paymentservice.payment.presentation.dto;

import com.node5.paymentservice.payment.application.dto.PaymentFailureCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PaymentFailureRequest(
        @NotBlank(message = "주문 아이디는 필수입니다.")
        String orderId,
        @NotBlank(message = "결제 키는 필수입니다.")
        String paymentKey,
        @NotBlank(message = "실패 코드는 필수입니다.")
        String code,
        @NotBlank(message = "실패 메시지는 필수입니다.")
        String message,
        @Positive(message = "결제 금액은 0보다 커야 합니다.")
        Long amount,
        String rawPayload
) {
    public PaymentFailureCommand toCommand() {
        return new PaymentFailureCommand(orderId, paymentKey, code, message, amount, rawPayload);
    }
}
