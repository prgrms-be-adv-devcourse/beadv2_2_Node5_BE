package com.node5.supportservice.review.infrastructure;

import com.node5.supportservice.review.domain.ReviewDetail;
import com.node5.supportservice.review.domain.ReviewDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewDetailRepositoryAdapter implements ReviewDetailRepository {
    private final ReviewDetailJpaRepository reviewDetailJpaRepository;

    @Override
    public Page<ReviewDetail> findByProductIdOrderByCreatedAtDesc(UUID productId, Pageable pageable) {
        return reviewDetailJpaRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
    }

    @Override
    public Page<ReviewDetail> findByMemberIdOrderByCreatedAtDesc(UUID memberId, Pageable pageable) {
        return reviewDetailJpaRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
    }

    @Override
    public Optional<ReviewDetail> findById(UUID id) {
        return reviewDetailJpaRepository.findById(id);
    }

    @Override
    public Page<ReviewDetail> findByMemberIdAndProductIdOrderByCreatedAtDesc(UUID memberId, UUID productId, Pageable pageable) {
        return reviewDetailJpaRepository.findByMemberIdAndProductIdOrderByCreatedAtDesc(memberId, productId, pageable);
    }

    @Override
    public ReviewDetail save(ReviewDetail reviewDetail) {
        return reviewDetailJpaRepository.save(reviewDetail);
    }

    @Override
    public void deleteById(UUID reviewId) {
        reviewDetailJpaRepository.deleteById(reviewId);
    }

    @Override
    public int incrementLikeCount(UUID reviewId) {
        return reviewDetailJpaRepository.incrementLikeCount(reviewId);
    }

    @Override
    public Boolean existsByProductIdAndMemberId(UUID productId, UUID memberId) {
        return reviewDetailJpaRepository.existsByProductIdAndMemberId(productId, memberId);
    }
}
