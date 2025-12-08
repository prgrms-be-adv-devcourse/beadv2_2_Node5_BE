package com.node5.walletservice.wallet.application.dto;

import com.node5.walletservice.wallet.domain.WalletWithdrawLog;

import java.util.UUID;

public record WalletWithdrawInfo(
        UUID id,
        UUID memberId,
        Long amount
) {

    public static WalletWithdrawInfo from(WalletWithdrawLog walletWithdrawLog) {
        return new WalletWithdrawInfo(
                walletWithdrawLog.getId(),
                walletWithdrawLog.getMemberId(),
                walletWithdrawLog.getAmount()
        );
    }
}
