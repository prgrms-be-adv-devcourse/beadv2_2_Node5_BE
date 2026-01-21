package com.node5.supportservice.reviewsummary.presentation;

import com.node5.supportservice.reviewsummary.application.ReviewSummaryService;
import com.node5.supportservice.reviewsummary.application.dto.ReviewSummaryInfoResponse;
import com.node5.supportservice.reviewsummary.presentation.dto.ReviewSummaryUpsertRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/review-summaries")
@RequiredArgsConstructor
public class ReviewSummaryInternalController {

    private final ReviewSummaryService reviewSummaryService;

    @GetMapping
    public ResponseEntity<ReviewSummaryInfoResponse> getReviewSummary(@RequestParam UUID productId) {
        return ResponseEntity.ok(reviewSummaryService.getReviewSummaries(productId));
    }

    @PostMapping
    public ResponseEntity<Void> upsertReviewSummary(@RequestBody ReviewSummaryUpsertRequest request) {
        reviewSummaryService.upsertReviewSummary(request.toCommand());
        return ResponseEntity.ok().build();
    }
}
