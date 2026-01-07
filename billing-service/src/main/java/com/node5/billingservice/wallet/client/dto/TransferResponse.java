package com.node5.billingservice.wallet.client.dto;

import com.node5.billingservice.wallet.client.TransferStateCode;

import java.time.LocalDateTime;

public record TransferResponse(
        TransferStateCode stateCode,
        String transactionId,
        String message,
        LocalDateTime requestedAt,
        LocalDateTime completedAt
) {
    public boolean isSuccess() {
        return stateCode == TransferStateCode.SUCCESS;
    }
}
