package com.node5.walletservice.wallet.presentation.dto;

import com.node5.walletservice.wallet.application.dto.WalletRefundCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record WalletRefundRequest(
        @NotNull(message = "주문 ID는 필수입니다.")
        UUID orderId,

        @NotNull(message = "환불 금액은 필수입니다.")
        @Positive(message = "환불 금액은 0보다 커야 합니다.")
        Long refundAmount
) {

    public WalletRefundCommand toCommand() {
        return new WalletRefundCommand(orderId, refundAmount);
    }
}
