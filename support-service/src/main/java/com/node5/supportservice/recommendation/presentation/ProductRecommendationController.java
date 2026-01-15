package com.node5.supportservice.recommendation.presentation;

import com.node5.supportservice.recommendation.application.ProductRecommendationService;
import com.node5.supportservice.recommendation.application.dto.ProductRecommendationInfo;
import com.node5.supportservice.recommendation.presentation.dto.ProductRecommendationRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.v1}/recommendation")
@RequiredArgsConstructor
public class ProductRecommendationController {

    private final ProductRecommendationService productRecommendationService;

    @Operation(summary = "상품 추천", description = "사용자 취향 임베딩을 기반으로 상품을 추천한다.")
    @PostMapping
    public ResponseEntity<ProductRecommendationInfo> recommend(@RequestBody ProductRecommendationRequest request) {
        ProductRecommendationInfo recommendationInfo = productRecommendationService.recommendProducts(
                request.preferenceEmbedding(),
                request.excludedProductIds(),
                request.limit()
        );
        return ResponseEntity.ok(recommendationInfo);
    }
}
