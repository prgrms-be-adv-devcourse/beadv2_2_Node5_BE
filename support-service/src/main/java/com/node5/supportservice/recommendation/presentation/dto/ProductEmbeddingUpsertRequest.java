package com.node5.supportservice.recommendation.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductEmbeddingUpsertRequest(
        @NotNull(message = "상품 ID는 필수입니다.")
        UUID productId,
        String name,
        String description,
        String category,
        String productStatus
) {
}
