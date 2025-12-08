package com.node5.walletservice.wallet.application.dto;

public record WalletChargeCommand(
        String paymentKey,
        Long amount
) {
}
