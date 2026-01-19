package com.node5.supportservice.review.presentation.dto;

import com.node5.supportservice.review.application.dto.ReviewStatusCommand;

import java.util.UUID;

public record ReviewStatusRequest(
        UUID orderId,
        UUID productId
) {
    public ReviewStatusCommand toCommand() {
        return new ReviewStatusCommand(orderId, productId);
    }
}
