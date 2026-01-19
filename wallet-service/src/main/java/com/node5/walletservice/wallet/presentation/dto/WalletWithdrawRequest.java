package com.node5.walletservice.wallet.presentation.dto;

import com.node5.walletservice.wallet.application.dto.WalletWithdrawCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record WalletWithdrawRequest(
        @NotNull(message = "주문 아이디는 필수입니다.")
        UUID orderId,
        @NotNull(message = "출금 금액은 필수입니다.")
        @Positive(message = "출금 금액은 0보다 커야 합니다.")
        Long withdrawAmount
) {

    public WalletWithdrawCommand toCommand() {
        return new WalletWithdrawCommand(orderId, withdrawAmount);
    }
}
