package com.node5.supportservice.reviewsummary.infrastructure;

import com.node5.supportservice.reviewsummary.domain.ReviewSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReviewSummaryJpaRepository extends JpaRepository<ReviewSummary, UUID> {
    Optional<ReviewSummary> findByProductId(UUID productId);
}
