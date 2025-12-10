package com.node5.billingservice.wallet.application.dto;

import com.node5.billingservice.wallet.domain.Wallet;

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
