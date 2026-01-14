package com.node5.supportservice.review.infrastructure;

import com.node5.supportservice.review.domain.ReviewStatic;
import com.node5.supportservice.review.domain.ReviewStaticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewStaticRepositoryAdapter implements ReviewStaticRepository {
    private final ReviewStaticJpaRepository reviewJpaRepository;

    @Override
    public ReviewStatic findByProductId(UUID productId) {
        return reviewJpaRepository.findByProductId(productId);
    }

    @Override
    public ReviewStatic save(ReviewStatic review) {
        return reviewJpaRepository.save(review);
    }

    @Override
    public void deleteByProductId(UUID productId) {
        reviewJpaRepository.deleteByProductId(productId);
    }

    @Override
    public void incrementStatistics(UUID productId, Integer rating) {
        reviewJpaRepository.incrementStatistics(productId, rating);
    }

    @Override
    public void decrementStatistics(UUID productId, Integer rating) {
        reviewJpaRepository.decrementStatistics(productId, rating);
    }

    @Override
    public Boolean existsByProductId(UUID productId) {
        return reviewJpaRepository.existsByProductId(productId);
    }

    @Override
    public void updateStatisticsOnReviewEdit(UUID productId, Integer oldRating, Integer newRating) {
        reviewJpaRepository.updateStatisticsOnReviewEdit(productId, oldRating, newRating);
    }
}
