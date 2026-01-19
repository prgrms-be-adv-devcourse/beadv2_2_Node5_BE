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
    public ResponseEntity<ProductRecommendationInfo> recommend(
            @RequestHeader("Member-Id") UUID memberId,
            @RequestBody ProductRecommendationRequest request) {
        ProductRecommendationInfo recommendationInfo = recommendationService.recommendProducts(
                memberId,
                request.cartItemProductIds(),
                request.limit()
        );
        return ResponseEntity.ok(recommendationInfo);
    }

    @Operation(summary = "취향 요약 추천", description = "장바구니 상품 기반으로 취향 요약과 임베딩을 생성한다. 테스트용 메소드.")
    @PostMapping("/taste")
    public ResponseEntity<RecommendationResponse> recommendTaste(
            @RequestHeader("Member-Id") UUID memberId,
            @RequestBody List<UUID> cartItemProductIds
    ) {
        RecommendationService.Result result = recommendationService.recommendTaste(memberId, cartItemProductIds);
        return ResponseEntity.ok(new RecommendationResponse(
            result.tasteSummary(),
            result.embedding()
        ));
    }
}
