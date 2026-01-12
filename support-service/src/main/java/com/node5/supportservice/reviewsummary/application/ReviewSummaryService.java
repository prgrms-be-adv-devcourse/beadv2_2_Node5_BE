package com.node5.supportservice.reviewsummary.application;

import com.node5.supportservice.reviewsummary.application.dto.ReviewSummaryInfoResponse;
import com.node5.supportservice.reviewsummary.domain.ReviewSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewSummaryService {
    private final ReviewSummaryRepository reviewSummaryRepository;

    public ReviewSummaryInfoResponse getReviewSummaries(UUID productId) {
        return reviewSummaryRepository.findByProductId(productId)
                .map(ReviewSummaryInfoResponse::from)
                .orElseGet(ReviewSummaryInfoResponse::empty);
    }
}
