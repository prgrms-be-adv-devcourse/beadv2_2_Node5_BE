package com.node5.orderservice.order.client.dto;

import java.util.UUID;

public record WalletWithdrawRequest (
        UUID orderId,
        Long withdrawAmount
) {
}
