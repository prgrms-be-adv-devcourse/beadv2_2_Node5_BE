package com.node5.supportservice.reviewsummary.application;

import com.node5.supportservice.reviewsummary.application.dto.ReviewSummaryInfoResponse;
import com.node5.supportservice.reviewsummary.domain.ReviewSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewSummaryService {
    private final ReviewSummaryRepository reviewSummaryRepository;

    public List<ReviewSummaryInfoResponse> getReviewSummaries(UUID productId) {
        return reviewSummaryRepository.findByProductIdOrderByRatingDesc(productId).stream()
                .map(ReviewSummaryInfoResponse::from)
                .toList();
    }
}
