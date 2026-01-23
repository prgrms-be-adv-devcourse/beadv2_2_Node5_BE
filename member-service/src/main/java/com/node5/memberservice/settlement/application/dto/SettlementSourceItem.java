package com.node5.memberservice.settlement.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SettlementSourceItem(
        UUID productId,
        UUID shopId,
        UUID orderId,
        BigDecimal itemAmount,
        LocalDateTime createdAt
) {
}
