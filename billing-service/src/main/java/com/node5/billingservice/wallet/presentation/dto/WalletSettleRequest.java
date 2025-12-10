package com.node5.billingservice.wallet.presentation.dto;

import com.node5.billingservice.wallet.application.dto.WalletSettleCommand;

public record WalletSettleRequest(
        Long amount
) {

    public WalletSettleCommand toCommand() {
        return new WalletSettleCommand(amount);
    }
}
