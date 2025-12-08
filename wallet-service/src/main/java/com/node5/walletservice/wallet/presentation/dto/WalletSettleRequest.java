package com.node5.walletservice.wallet.presentation.dto;

import com.node5.walletservice.wallet.application.dto.WalletSettleCommand;

public record WalletSettleRequest(
        Long amount
) {

    public WalletSettleCommand toCommand() {
        return new WalletSettleCommand(amount);
    }
}
