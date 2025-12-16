package com.node5.orderservice.order.client.dto;

import java.util.UUID;

public record WalletInfo (
        UUID id,
        UUID memberId,
        Long balance
) {
}
