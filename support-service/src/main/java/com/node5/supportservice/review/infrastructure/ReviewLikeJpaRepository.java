package com.node5.supportservice.review.infrastructure;

import com.node5.supportservice.review.domain.ReviewLikeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewLikeJpaRepository extends JpaRepository<ReviewLikeHistory, UUID> {
    Boolean existsByReviewIdAndMemberId(UUID reviewId, UUID memberId);
}
