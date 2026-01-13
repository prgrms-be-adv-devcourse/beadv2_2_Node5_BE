package com.node5.supportservice.review.application.dto;

import com.node5.supportservice.review.domain.ReviewDetail;

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
    public static ReviewDetailInfo from(ReviewDetail reviewDetail) {
        return new ReviewDetailInfo(
                reviewDetail.getId(),
                reviewDetail.getProductId(),
                reviewDetail.getNickname(),
                reviewDetail.getRating(),
                reviewDetail.getBody(),
                reviewDetail.getLikeCount(),
                reviewDetail.getCreatedAt()
        );
    }
}
