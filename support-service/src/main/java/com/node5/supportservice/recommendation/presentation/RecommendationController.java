package com.node5.supportservice.recommendation.presentation;

import com.node5.supportservice.recommendation.application.RecommendationService;
import com.node5.supportservice.recommendation.application.dto.ProductRecommendationInfo;
import com.node5.supportservice.recommendation.application.dto.RecommendationResponse;
import com.node5.supportservice.recommendation.presentation.dto.ProductRecommendationRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.v1}/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "상품 추천", description = "사용자 취향 임베딩을 기반으로 상품을 추천한다.")
    @PostMapping
    public ResponseEntity<ProductRecommendationInfo> recommend(@RequestBody ProductRecommendationRequest request) {
        ProductRecommendationInfo recommendationInfo = recommendationService.recommendProducts(
                request.preferenceEmbedding(),
                request.excludedProductIds(),
                request.limit()
        );
        return ResponseEntity.ok(recommendationInfo);
    }

    @PostMapping("/taste")
    public ResponseEntity<RecommendationResponse> recommendTaste(
            @RequestHeader("Member-Id") UUID memberId,
            @RequestBody List<UUID> cartItemIds
    ) {
        RecommendationService.Result result = recommendationService.recommendTaste(memberId, cartItemIds);
        return ResponseEntity.ok(new RecommendationResponse(
            result.tasteSummary(),
            result.embedding()
        ));
    }
}
