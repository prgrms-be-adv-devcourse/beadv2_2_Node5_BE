package com.node5.billingservice.wallet.application.dto;

import java.util.UUID;

public record WalletRefundCommand(
        UUID walletDepositLogId,
        String paymentKey
) {
}
