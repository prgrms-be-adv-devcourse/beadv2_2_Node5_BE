package com.node5.billingservice.wallet.presentation.dto;

import com.node5.billingservice.wallet.application.dto.WalletRefundCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record WalletRefundRequest(
        @NotNull(message = "입금id는 필수입니다.")
        UUID walletDepositLogId,

        @NotBlank(message = "결제 키는 필수입니다.")
        String paymentKey
) {

    public WalletRefundCommand toCommand() {
        return new WalletRefundCommand(walletDepositLogId, paymentKey);
    }
}
