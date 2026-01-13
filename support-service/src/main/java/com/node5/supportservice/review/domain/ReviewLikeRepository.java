package com.node5.supportservice.review.domain;

import java.util.UUID;

public interface ReviewLikeRepository {
    Boolean existsByReviewIdAndMemberId(UUID reviewId, UUID memberId);

    ReviewLikeHistory save(ReviewLikeHistory reviewLike);
}
