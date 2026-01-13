package com.node5.supportservice.recommendation.presentation;

import com.node5.supportservice.recommendation.application.RecommendationService;
import com.node5.supportservice.recommendation.presentation.dto.RecommendationRequest;
import com.node5.supportservice.recommendation.application.dto.RecommendationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.v1}/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    // TODO 임시 확인용
    @PostMapping
    public ResponseEntity<RecommendationResponse> recommend(
        @Valid @RequestBody RecommendationRequest request
    ) {
        RecommendationService.Result result = recommendationService.recommend(
            request.getOrderItems(),
            request.getCartItemIds()
        );
        return ResponseEntity.ok(new RecommendationResponse(
            result.tasteSummary(),
            result.embedding()
        ));
    }
}
