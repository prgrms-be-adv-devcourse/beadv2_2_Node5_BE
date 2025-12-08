package com.node5.walletservice.wallet.presentation.dto;

import com.node5.walletservice.wallet.application.dto.WalletRefundCommand;

import java.util.UUID;

public record WalletRefundRequest(
        UUID walletDepositLogId,
        String paymentKey
) {

    public WalletRefundCommand toCommand() {
        return new WalletRefundCommand(walletDepositLogId, paymentKey);
    }
}
