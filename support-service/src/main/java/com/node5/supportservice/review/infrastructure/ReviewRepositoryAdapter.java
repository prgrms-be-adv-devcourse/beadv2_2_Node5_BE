package com.node5.supportservice.review.infrastructure;

import com.node5.supportservice.review.domain.Review;
import com.node5.supportservice.review.domain.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryAdapter implements ReviewRepository {
    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public Review findByProductId(UUID productId) {
        return reviewJpaRepository.findByProductId(productId);
    }

    @Override
    public Review save(Review review) {
        return reviewJpaRepository.save(review);
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
