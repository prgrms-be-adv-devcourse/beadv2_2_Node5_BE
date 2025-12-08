package com.node5.walletservice.wallet.application.dto;

import java.util.UUID;

public record WalletRefundCommand(
        UUID walletDepositLogId,
        String paymentKey
) {
}
