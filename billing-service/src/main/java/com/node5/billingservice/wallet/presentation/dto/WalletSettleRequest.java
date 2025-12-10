package com.node5.billingservice.wallet.presentation.dto;

import com.node5.billingservice.wallet.application.dto.WalletSettleCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WalletSettleRequest(
        @NotNull(message = "정산 금액은 필수입니다.")
        @Positive(message = "정산 금액은 0보다 커야 합니다.")
        Long amount
) {

    public WalletSettleCommand toCommand() {
        return new WalletSettleCommand(amount);
    }
}
