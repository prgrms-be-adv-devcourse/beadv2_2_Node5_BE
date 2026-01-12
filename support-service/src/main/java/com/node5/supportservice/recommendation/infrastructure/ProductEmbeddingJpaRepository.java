package com.node5.supportservice.recommendation.infrastructure;

import com.node5.supportservice.recommendation.domain.ProductEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductEmbeddingJpaRepository extends JpaRepository<ProductEmbedding, UUID> {
    Optional<ProductEmbedding> findByProductId(UUID productId);
}