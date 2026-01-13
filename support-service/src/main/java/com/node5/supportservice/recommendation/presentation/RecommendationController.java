package com.node5.supportservice.recommendation.presentation;

import com.node5.supportservice.recommendation.application.RecommendationService;
import com.node5.supportservice.recommendation.application.dto.RecommendationResponse;
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

    @PostMapping
    public ResponseEntity<RecommendationResponse> recommend(
            @RequestHeader("Member-Id") UUID memberId,
            @RequestBody List<UUID> cartItemIds
    ) {
        RecommendationService.Result result = recommendationService.recommend(memberId, cartItemIds);
        return ResponseEntity.ok(new RecommendationResponse(
            result.tasteSummary(),
            result.embedding()
        ));
    }
}
