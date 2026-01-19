package com.node5.walletservice.wallet.application.dto;

import java.util.UUID;

public record WalletPaymentCommand(
        UUID memberId,
        String orderId,
        Long amount
) {
}
