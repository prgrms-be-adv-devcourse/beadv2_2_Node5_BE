package com.node5.billingservice.wallet.presentation.dto;

import com.node5.billingservice.wallet.application.dto.WalletChargeCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WalletChargeRequest(
        @NotBlank(message = "결제 키는 필수입니다.")
        String paymentKey,

        @NotNull(message = "충전 금액은 필수입니다.")
        @Positive(message = "충전 금액은 0보다 커야 합니다.")
        Long amount
) {

    public WalletChargeCommand toCommand() {
        return new WalletChargeCommand(paymentKey, amount);
    }
}
