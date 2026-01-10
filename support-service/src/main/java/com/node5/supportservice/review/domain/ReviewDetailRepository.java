package com.node5.supportservice.review.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ReviewDetailRepository {
    Page<ReviewDetail> findByProductIdOrderByCreatedAtDesc(UUID productId, Pageable pageable);

    Page<ReviewDetail> findByMemberIdOrderByCreatedAtDesc(UUID memberId, Pageable pageable);

    Optional<ReviewDetail> findById(UUID id);

    Page<ReviewDetail> findByMemberIdAndProductIdOrderByCreatedAtDesc(UUID memberId, UUID productId, Pageable pageable);

    ReviewDetail save(ReviewDetail reviewDetail);

    void deleteById(UUID reviewId);

    int incrementLikeCount(UUID reviewId);

    Boolean existsByProductIdAndMemberId(UUID productId, UUID memberId);
}
