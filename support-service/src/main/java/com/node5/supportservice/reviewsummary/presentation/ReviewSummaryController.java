package com.node5.supportservice.reviewsummary.presentation;

import com.node5.supportservice.reviewsummary.application.ReviewSummaryService;
import com.node5.supportservice.reviewsummary.application.dto.ReviewSummaryInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.v1}/review-summaries")
@RequiredArgsConstructor
public class ReviewSummaryController {
    private final ReviewSummaryService reviewSummaryService;

    @GetMapping
    public ResponseEntity<List<ReviewSummaryInfoResponse>> getReviewSummaries(@RequestParam UUID productId) {
        return ResponseEntity.ok(reviewSummaryService.getReviewSummaries(productId));
    }
}
