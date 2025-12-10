package com.node5.billingservice.wallet.application.dto;

public record WalletChargeCommand(
        String paymentKey,
        Long amount
) {
}
