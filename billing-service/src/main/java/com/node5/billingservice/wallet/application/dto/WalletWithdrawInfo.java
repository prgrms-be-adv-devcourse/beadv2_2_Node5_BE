package com.node5.billingservice.wallet.application.dto;

import com.node5.billingservice.wallet.domain.WalletWithdrawLog;
import com.node5.billingservice.wallet.domain.WalletWithdrawLogState;

import java.util.UUID;

public record WalletWithdrawInfo(
        UUID id,
        UUID memberId,
        UUID orderId,
        Long amount,
        WalletWithdrawLogState state
) {

    public static WalletWithdrawInfo from(WalletWithdrawLog walletWithdrawLog) {
        return new WalletWithdrawInfo(
                walletWithdrawLog.getId(),
                walletWithdrawLog.getMemberId(),
                walletWithdrawLog.getOrderId(),
                walletWithdrawLog.getAmount(),
                walletWithdrawLog.getState()
        );
    }
}
