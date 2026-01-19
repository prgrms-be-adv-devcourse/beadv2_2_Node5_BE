package com.node5.walletservice.wallet.application.dto;

public record WalletTransferCommand(
        String toAccountNo,
        Long transferAmount
) {
}
