package com.node5.orderservice.global.exception.openfeign.client.dto;

import java.util.UUID;

public record WalletInfo (
        UUID id,
        UUID memberId,
        Long balance
) {
}
