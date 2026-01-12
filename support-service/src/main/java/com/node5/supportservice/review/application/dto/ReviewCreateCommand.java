package com.node5.supportservice.review.application.dto;

import java.util.UUID;

public record ReviewCreateCommand(
        UUID productId,
        UUID orderId,
        Integer rating,
        String body
) {
}
