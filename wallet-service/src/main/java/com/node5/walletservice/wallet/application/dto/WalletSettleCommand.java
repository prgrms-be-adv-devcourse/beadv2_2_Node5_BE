package com.node5.walletservice.wallet.application.dto;

import java.util.UUID;

public record WalletSettleCommand(
        UUID settlementId,
        Long amount
) {
}
