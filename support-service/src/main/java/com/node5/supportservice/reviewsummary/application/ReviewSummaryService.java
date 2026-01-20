package com.node5.supportservice.reviewsummary.application;

import com.node5.supportservice.reviewsummary.application.dto.ReviewSummaryInfoResponse;
import com.node5.supportservice.reviewsummary.application.dto.ReviewSummaryUpsertCommand;
import com.node5.supportservice.reviewsummary.domain.ReviewSummary;
import com.node5.supportservice.reviewsummary.domain.ReviewSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewSummaryService {
    private final ReviewSummaryRepository reviewSummaryRepository;

    public ReviewSummaryInfoResponse getReviewSummaries(UUID productId) {
        return reviewSummaryRepository.findByProductId(productId)
                .map(ReviewSummaryInfoResponse::from)
                .orElse(null);
    }

    @Transactional
    public void upsertReviewSummary(ReviewSummaryUpsertCommand command) {
        Optional<ReviewSummary> summary = reviewSummaryRepository.findByProductId(command.productId());

        if (summary.isPresent()) {
            summary.get().update(command.summary(), command.endDate());
        } else {
            ReviewSummary newSummary = ReviewSummary.create(command.productId(), command.summary(), command.endDate());
            reviewSummaryRepository.save(newSummary);
        }
    }
}
