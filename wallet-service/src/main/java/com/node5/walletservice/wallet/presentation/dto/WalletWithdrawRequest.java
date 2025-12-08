package com.node5.walletservice.wallet.presentation.dto;

import com.node5.walletservice.wallet.application.dto.WalletWithdrawCommand;

public record WalletWithdrawRequest(
    Long amount
) {

    public WalletWithdrawCommand toCommand() {
        return new WalletWithdrawCommand(amount);
    }
}
