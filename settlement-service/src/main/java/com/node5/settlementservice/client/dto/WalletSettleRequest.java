package com.node5.settlementservice.client.dto;

import java.util.UUID;

public record WalletSettleRequest(
        UUID settlementId,
        Long amount
) {
}
