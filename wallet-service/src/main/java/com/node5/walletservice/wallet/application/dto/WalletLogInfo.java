package com.node5.walletservice.wallet.application.dto;

import com.node5.walletservice.wallet.domain.WalletTransactionLog;
import com.node5.walletservice.wallet.domain.WalletTransactionLogStatus;
import com.node5.walletservice.wallet.domain.WalletTransactionLogType;

import java.time.LocalDateTime;

public record WalletLogInfo(
        WalletTransactionLogType type,
        Long amount,
        Long balanceAfter,
        WalletTransactionLogStatus status,
        LocalDateTime createdAt
) {
    public static WalletLogInfo from(WalletTransactionLog log) {
        return new WalletLogInfo(
                log.getType(),
                log.getAmount(),
                log.getBalanceAfter(),
                log.getStatus(),
                log.getCreatedAt()
        );
    }
}
