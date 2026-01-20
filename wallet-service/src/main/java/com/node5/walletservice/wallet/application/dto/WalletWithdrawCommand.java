package com.node5.walletservice.wallet.application.dto;

import java.util.UUID;

public record WalletWithdrawCommand(
        UUID orderId,
        Long withdrawAmount
) {
}
