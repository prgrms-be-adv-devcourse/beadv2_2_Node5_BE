package com.node5.billingservice.wallet.presentation.dto;

import com.node5.billingservice.wallet.application.dto.WalletWithdrawCommand;

public record WalletWithdrawRequest(
    Long amount
) {

    public WalletWithdrawCommand toCommand() {
        return new WalletWithdrawCommand(amount);
    }
}
