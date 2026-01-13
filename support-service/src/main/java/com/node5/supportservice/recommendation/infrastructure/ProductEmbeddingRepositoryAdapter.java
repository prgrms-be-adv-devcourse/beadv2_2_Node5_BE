package com.node5.supportservice.recommendation.infrastructure;

import com.node5.supportservice.recommendation.domain.ProductEmbedding;
import com.node5.supportservice.recommendation.domain.ProductEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductEmbeddingRepositoryAdapter implements ProductEmbeddingRepository {

    private final ProductEmbeddingJpaRepository productEmbeddingJpaRepository;

    @Override
    public Optional<ProductEmbedding> findByProductId(UUID productId) {
        return productEmbeddingJpaRepository.findByProductId(productId);
    }

    @Override
    public ProductEmbedding save(ProductEmbedding productEmbedding) {
        return productEmbeddingJpaRepository.save(productEmbedding);
    }

    @Override
    public List<UUID> findSimilarActiveProductIds(float[] preferenceEmbedding, int limit) {
        return productEmbeddingJpaRepository.findSimilarActiveProductIds(preferenceEmbedding, limit);
    }

    @Override
    public List<UUID> findSimilarActiveProductIdsExcluding(float[] preferenceEmbedding, List<UUID> excludedProductIds, int limit) {
        return productEmbeddingJpaRepository.findSimilarActiveProductIdsExcluding(preferenceEmbedding, excludedProductIds, limit);
    }
}
