package com.node5.settlementservice.client.dto;

import java.util.UUID;

public record WalletInfo (
        UUID id,
        UUID memberId,
        Long balance
) {
}
