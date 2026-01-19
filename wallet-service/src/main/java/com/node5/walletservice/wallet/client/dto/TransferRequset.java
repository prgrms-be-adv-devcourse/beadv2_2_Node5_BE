package com.node5.walletservice.wallet.client.dto;

public record TransferRequset(
        String toAccountId,
        Long amount,
        String orderId
) {
}
