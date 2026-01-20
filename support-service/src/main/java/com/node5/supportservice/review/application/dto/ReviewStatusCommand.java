package com.node5.supportservice.review.application.dto;

import java.util.UUID;

public record ReviewStatusCommand(
        UUID orderId, UUID productId
) {
}
