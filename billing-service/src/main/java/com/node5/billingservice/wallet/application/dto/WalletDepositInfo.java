package com.node5.billingservice.wallet.application.dto;

import com.node5.billingservice.wallet.domain.WalletDepositLog;

import java.util.UUID;

public record WalletDepositInfo(
        UUID id,
        UUID memberId,
        UUID settlementId,
        Long amount
) {

    public static WalletDepositInfo from(WalletDepositLog walletDepositLog) {
        return new WalletDepositInfo(
                walletDepositLog.getId(),
                walletDepositLog.getMemberId(),
                walletDepositLog.getSettlementId(),
                walletDepositLog.getAmount()
        );
    }
}
