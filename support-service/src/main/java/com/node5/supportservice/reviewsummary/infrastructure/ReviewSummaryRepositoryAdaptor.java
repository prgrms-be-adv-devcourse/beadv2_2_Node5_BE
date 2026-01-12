package com.node5.supportservice.reviewsummary.infrastructure;

import com.node5.supportservice.reviewsummary.domain.ReviewSummary;
import com.node5.supportservice.reviewsummary.domain.ReviewSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewSummaryRepositoryAdaptor implements ReviewSummaryRepository {
    private final ReviewSummaryJpaRepository reviewSummaryJpaRepository;

    @Override
    public Optional<ReviewSummary> findByProductId(UUID productId) {
        return reviewSummaryJpaRepository.findByProductId(productId);
    }
}
