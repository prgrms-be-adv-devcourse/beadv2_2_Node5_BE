package com.node5.settlementservice.settlement.client.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record WalletSettleInfo(
        UUID id,
        UUID memberId,
        Long balance,
        LocalDateTime payoutAt
) {
}
