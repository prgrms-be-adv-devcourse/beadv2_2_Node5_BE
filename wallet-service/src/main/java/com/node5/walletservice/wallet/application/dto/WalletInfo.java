package com.node5.walletservice.wallet.application.dto;

import com.node5.walletservice.wallet.domain.Wallet;

import java.util.UUID;

public record WalletInfo(
        UUID id,
        UUID memberId,
        Long balance
) {

    public static WalletInfo from(Wallet wallet) {
        return new WalletInfo(
                wallet.getId(),
                wallet.getMemberId(),
                wallet.getBalance()
        );
    }
}
