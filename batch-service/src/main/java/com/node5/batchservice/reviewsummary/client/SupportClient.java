package com.node5.batchservice.reviewsummary.client;

import com.node5.batchservice.reviewsummary.client.dto.ReviewDetailInfo;
import com.node5.batchservice.reviewsummary.client.dto.ReviewSearchSimilarRequest;
import com.node5.batchservice.reviewsummary.client.dto.ReviewSummaryInfoResponse;
import com.node5.batchservice.reviewsummary.client.dto.ReviewSummaryUpsertRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "support-service")
public interface SupportClient {

    @PostMapping("/internal/reviews/search-similar/{productId}")
    ResponseEntity<List<ReviewDetailInfo>> searchSimilarReviews(
            @PathVariable UUID productId,
            @RequestBody ReviewSearchSimilarRequest request
    );

    @GetMapping("/internal/review-summaries")
    ResponseEntity<ReviewSummaryInfoResponse> getReviewSummary(@RequestParam UUID productId);

    @PostMapping("/internal/review-summaries")
    ResponseEntity<Void> upsertReviewSummary(@RequestBody ReviewSummaryUpsertRequest request);

    @PostMapping("/reindex-embeddings")
    ResponseEntity<Void> reindexReviewEmbeddings();
}
