package com.node5.orderservice.order.client.dto;

import java.util.UUID;

public record WalletRefundRequest(
        UUID orderId,
        Long refundAmount
) {
}
