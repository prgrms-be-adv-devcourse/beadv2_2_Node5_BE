package com.node5.orderservice.global.exception.openfeign.client.dto;

import java.util.UUID;

public record WalletRefundRequest(
        UUID orderId,
        Long refundAmount
) {
}
