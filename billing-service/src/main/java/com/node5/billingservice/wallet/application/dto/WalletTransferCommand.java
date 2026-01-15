package com.node5.billingservice.wallet.application.dto;

public record WalletTransferCommand(
        String toAccountNo,
        Long transferAmount
) {
}
