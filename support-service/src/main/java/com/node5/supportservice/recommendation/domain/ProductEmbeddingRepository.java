package com.node5.supportservice.recommendation.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductEmbeddingRepository {
    Optional<ProductEmbedding> findByProductId(UUID productId);

    ProductEmbedding save(ProductEmbedding productEmbedding);

    List<UUID> findSimilarActiveProductIds(float[] preferenceEmbedding, int limit);

    List<UUID> findSimilarActiveProductIdsExcluding(float[] preferenceEmbedding, List<UUID> excludedProductIds, int limit);
}
