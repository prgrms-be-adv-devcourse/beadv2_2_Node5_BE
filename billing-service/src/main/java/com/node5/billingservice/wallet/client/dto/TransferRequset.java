package com.node5.billingservice.wallet.client.dto;

public record TransferRequset(
        String toAccountId,
        Long amount,
        String orderId
) {
}
