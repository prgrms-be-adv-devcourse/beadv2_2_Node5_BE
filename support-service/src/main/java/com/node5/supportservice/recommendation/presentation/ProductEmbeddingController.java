package com.node5.supportservice.recommendation.presentation;

import com.node5.supportservice.recommendation.application.ProductEmbeddingService;
import com.node5.supportservice.recommendation.presentation.dto.ProductEmbeddingUpsertRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${api.v1}/recommendations/embeddings")
@RequiredArgsConstructor
public class ProductEmbeddingController {

    private final ProductEmbeddingService productEmbeddingService;

    @Operation(summary = "상품 임베딩 업서트", description = "상품 메타데이터로 임베딩을 생성/갱신한다. 테스트용 메소드.")
    @PostMapping
    public ResponseEntity<Void> upsertEmbedding(@Valid @RequestBody ProductEmbeddingUpsertRequest request) {
        productEmbeddingService.upsertEmbedding(
                request.productId(),
                request.name(),
                request.description(),
                request.category(),
                request.productStatus()
        );
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "상품 임베딩 삭제", description = "상품 ID에 해당하는 임베딩을 삭제한다. 테스트용 메소드.")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteEmbedding(@PathVariable UUID productId) {
        productEmbeddingService.deleteEmbedding(productId);
        return ResponseEntity.ok().build();
    }
}
