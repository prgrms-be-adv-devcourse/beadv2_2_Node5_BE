package com.node5.billingservice.wallet.application.dto;

import java.util.UUID;

public record WalletSettleCommand(
        UUID settlementId,
        Long amount
) {
}
