package com.node5.supportservice.recommendation.domain;

import java.util.List;
import java.util.UUID;

public interface ProductEmbeddingRepository {
    ProductEmbedding save(ProductEmbedding productEmbedding);

    void markDeletedByProductId(UUID productId);

    List<UUID> findSimilarActiveProductIds(float[] preferenceEmbedding, int limit);

    List<UUID> findSimilarActiveProductIdsExcluding(float[] preferenceEmbedding, List<UUID> excludedProductIds, int limit);
}
