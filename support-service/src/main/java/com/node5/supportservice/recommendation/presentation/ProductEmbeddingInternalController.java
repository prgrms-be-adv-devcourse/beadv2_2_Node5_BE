package com.node5.supportservice.recommendation.presentation;

import com.node5.supportservice.recommendation.application.ProductEmbeddingService;
import com.node5.supportservice.recommendation.presentation.dto.ProductEmbeddingBackfillResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("internal/recommendations/embeddings")
@RequiredArgsConstructor
public class ProductEmbeddingInternalController {

    private final ProductEmbeddingService productEmbeddingService;

    @Operation(summary = "상품 임베딩 수동 생성", description = "카탈로그 서비스에서 상품 ID를 조회해 임베딩을 생성합니다.")
    @PostMapping("/backfill")
    public ResponseEntity<ProductEmbeddingBackfillResponse> backfillEmbeddings(
            @RequestHeader("Member-Id") UUID memberId,
            @RequestParam(defaultValue = "200") int size,
            @RequestParam(required = false) Integer maxPages
    ) {
        ProductEmbeddingService.BackfillResult result = productEmbeddingService.backfillEmbeddings(memberId, size, maxPages);
        return ResponseEntity.ok(new ProductEmbeddingBackfillResponse(result.processed(), result.pages(), result.pageSize()));
    }
}
