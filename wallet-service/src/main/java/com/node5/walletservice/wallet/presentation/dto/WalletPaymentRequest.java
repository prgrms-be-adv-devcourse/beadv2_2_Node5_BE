package com.node5.walletservice.wallet.presentation.dto;

import com.node5.walletservice.wallet.application.dto.WalletPaymentCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record WalletPaymentRequest(
        @NotNull(message = "회원 ID는 필수입니다.")
        UUID memberId,   // 회원 식별자
        @NotBlank(message = "주문 ID는 필수입니다.")
        String orderId,  // 주문 번호 (상태 추적 및 멱등성 보장용)
        @NotNull(message = "요청 금액은 필수입니다.")
        @Positive(message = "요청 금액은 0보다 커야 합니다.")
        Long amount      // 요청 금액
) {
    public WalletPaymentCommand toCommand() {
        return new WalletPaymentCommand(memberId, orderId, amount);
    }
}