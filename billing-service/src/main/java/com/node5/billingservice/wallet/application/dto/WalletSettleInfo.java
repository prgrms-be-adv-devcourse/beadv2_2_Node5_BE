package com.node5.billingservice.wallet.application.dto;

import com.node5.billingservice.wallet.domain.Wallet;

import java.time.LocalDateTime;
import java.util.UUID;

public record WalletSettleInfo(
        UUID id,
        UUID memberId,
        Long balance,
        LocalDateTime payoutAt
) {
    public static WalletSettleInfo from(Wallet wallet, LocalDateTime payoutAt) {
        return new WalletSettleInfo(
                wallet.getId(),
                wallet.getMemberId(),
                wallet.getBalance(),
                payoutAt
        );
    }
}
