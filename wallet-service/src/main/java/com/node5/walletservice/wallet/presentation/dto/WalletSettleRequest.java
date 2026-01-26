package com.node5.walletservice.wallet.presentation.dto;

import com.node5.walletservice.wallet.application.dto.WalletSettleCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record WalletSettleRequest(
        @NotNull(message = "정산 아이디는 필수입니다.")
        UUID settlementId,
        @NotNull(message = "정산 금액은 필수입니다.")
        @Positive(message = "정산 금액은 0보다 커야 합니다.")
        Long amount
) {

    public WalletSettleCommand toCommand() {
        return new WalletSettleCommand(settlementId, amount);
    }
}
