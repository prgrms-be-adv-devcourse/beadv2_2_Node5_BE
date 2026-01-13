package com.node5.supportservice.recommendation.domain;

import java.util.Optional;
import java.util.UUID;

public interface ProductEmbeddingRepository {
    Optional<ProductEmbedding> findByProductId(UUID productId);

    ProductEmbedding save(ProductEmbedding productEmbedding);
}
