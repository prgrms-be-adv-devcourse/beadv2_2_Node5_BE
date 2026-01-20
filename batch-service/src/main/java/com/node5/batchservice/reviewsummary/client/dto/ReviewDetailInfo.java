package com.node5.batchservice.reviewsummary.client.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewDetailInfo(
        UUID reviewId,
        UUID productId,
        String nickname,
        Integer rating,
        String body,
        Integer likeCount,
        LocalDateTime createdAt
) {
}
