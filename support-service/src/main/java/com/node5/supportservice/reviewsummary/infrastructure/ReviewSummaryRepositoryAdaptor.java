package com.node5.supportservice.reviewsummary.infrastructure;

import com.node5.supportservice.reviewsummary.domain.ReviewSummary;
import com.node5.supportservice.reviewsummary.domain.ReviewSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewSummaryRepositoryAdaptor implements ReviewSummaryRepository {
    private final ReviewSummaryJpaRepository reviewSummaryJpaRepository;

    @Override
    public List<ReviewSummary> findByProductIdOrderByRatingDesc(UUID productId) {
        return reviewSummaryJpaRepository.findByProductIdOrderByRatingDesc(productId);
    }
}
