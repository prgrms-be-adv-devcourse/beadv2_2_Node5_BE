package com.node5.billingservice.wallet.presentation.dto;

import com.node5.billingservice.wallet.application.dto.WalletWithdrawCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WalletWithdrawRequest(
        @NotNull(message = "출금 금액은 필수입니다.")
        @Positive(message = "출금 금액은 0보다 커야 합니다.")
        Long amount
) {

    public WalletWithdrawCommand toCommand() {
        return new WalletWithdrawCommand(amount);
    }
}
