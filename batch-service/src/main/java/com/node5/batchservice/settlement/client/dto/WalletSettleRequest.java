package com.node5.batchservice.settlement.client.dto;

import java.util.UUID;

public record WalletSettleRequest(
        UUID settlementId,
        Long amount
) {
}
