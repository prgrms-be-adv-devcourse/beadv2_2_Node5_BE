package com.node5.billingservice.wallet.presentation.dto;

import com.node5.billingservice.wallet.application.dto.WalletChargeCommand;

public record WalletChargeRequest(
        String paymentKey,
        Long amount
) {

    public WalletChargeCommand toCommand() {
        return new WalletChargeCommand(paymentKey, amount);
    }
}
