package com.node5.billingservice.wallet.application.dto;

import com.node5.billingservice.wallet.domain.WalletTransferLog;

import java.time.LocalDateTime;

public record WalletTransferInfo(
        Long amount,
        String transactionId,
        String message,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt

) {
    public static WalletTransferInfo from(WalletTransferLog walletTransferLog) {
        return new WalletTransferInfo(
                walletTransferLog.getAmount(),
                walletTransferLog.getTransactionId(),
                walletTransferLog.getMessage(),
                walletTransferLog.getRequestedAt(),
                walletTransferLog.getApprovedAt()
        );
    }
}
