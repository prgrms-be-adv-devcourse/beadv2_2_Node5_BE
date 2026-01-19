package com.node5.supportservice.global.openfeign.client.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderStatusRequest(
        @NotNull(message = "orderId는 필수입니다.")
        UUID orderId,
        @NotNull(message = "productId는 필수입니다.")
        UUID productId
) {
}
