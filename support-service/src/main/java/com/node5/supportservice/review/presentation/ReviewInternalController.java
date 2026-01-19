package com.node5.supportservice.review.presentation;

import com.node5.supportservice.review.application.ReviewService;
import com.node5.supportservice.review.application.dto.ReviewDetailInfo;
import com.node5.supportservice.review.presentation.dto.ReviewSearchSimilarRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("internal/reviews")
@RequiredArgsConstructor
public class ReviewInternalController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 유사도 검색", description = "리뷰 임베딩 벡터를 기반으로 유사한 리뷰를 검색합니다.")
    @PostMapping("/search-similar/{productId}")
    public ResponseEntity<List<ReviewDetailInfo>> searchSimilarReviews(@PathVariable UUID productId, @RequestBody ReviewSearchSimilarRequest request) {
        return ResponseEntity.ok(reviewService.searchSimilarReviewDetails(productId, request.toCommand(request)));
    }

    @Operation(summary = "리뷰 임베딩 재생성", description = "모든 리뷰의 임베딩 벡터를 재생성합니다.")
    @PostMapping("/regenerate-embeddings")
    public ResponseEntity<Void> regenerateReviewEmbeddings() {
        reviewService.recreateAllReviewEmbeddings();
        return ResponseEntity.ok().build();
    }
}
