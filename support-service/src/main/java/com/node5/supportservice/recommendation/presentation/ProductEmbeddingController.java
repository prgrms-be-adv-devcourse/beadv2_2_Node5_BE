package com.node5.supportservice.recommendation.presentation;

import com.node5.supportservice.recommendation.application.ProductEmbeddingService;
import com.node5.supportservice.recommendation.presentation.dto.ProductEmbeddingUpsertRequest;
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

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteEmbedding(@PathVariable UUID productId) {
        productEmbeddingService.deleteEmbedding(productId);
        return ResponseEntity.ok().build();
    }
}
