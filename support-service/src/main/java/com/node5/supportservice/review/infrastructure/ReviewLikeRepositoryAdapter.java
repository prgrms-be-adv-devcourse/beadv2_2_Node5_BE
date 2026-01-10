package com.node5.supportservice.review.infrastructure;

import com.node5.supportservice.review.domain.ReviewLike;
import com.node5.supportservice.review.domain.ReviewLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewLikeRepositoryAdapter implements ReviewLikeRepository {
    private final ReviewLikeJpaRepository reviewLikeJpaRepository;

    @Override
    public Boolean existsByReviewIdAndMemberId(UUID reviewId, UUID memberId) {
        return reviewLikeJpaRepository.existsByReviewIdAndMemberId(reviewId, memberId);
    }

    @Override
    public ReviewLike save(ReviewLike reviewLike) {
        return reviewLikeJpaRepository.save(reviewLike);
    }
}
