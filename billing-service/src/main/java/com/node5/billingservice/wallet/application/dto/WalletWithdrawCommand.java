package com.node5.billingservice.wallet.application.dto;

import java.util.UUID;

public record WalletWithdrawCommand(
        UUID orderId,
        Long withdrawAmount
) {
}
