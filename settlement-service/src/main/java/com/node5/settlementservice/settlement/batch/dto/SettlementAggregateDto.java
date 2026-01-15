package com.node5.settlementservice.settlement.batch.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SettlementAggregateDto(
        UUID shopId,
        BigDecimal totalAmount //판매자의 정산 대상 달의 매출액
) {
}
