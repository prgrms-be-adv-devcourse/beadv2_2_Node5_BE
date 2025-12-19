package com.node5.shopservice.shop.client.dto;

import java.util.UUID;

public record WalletInfo(
        UUID id,
        UUID memberId,
        Long balance
) {
}
