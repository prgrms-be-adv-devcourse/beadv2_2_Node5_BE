package com.node5.supportservice.recommendation.infrastructure;

import com.node5.supportservice.recommendation.domain.ProductEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductEmbeddingJpaRepository extends JpaRepository<ProductEmbedding, UUID> {
    Optional<ProductEmbedding> findByProductId(UUID productId);

    @Query(value = """
            select product_id
            from support.product_embedding
            where status = 'ACTIVE'
            order by embedding <-> CAST(:embedding AS vector)
            limit :limit
            """, nativeQuery = true)
    List<UUID> findSimilarActiveProductIds(@Param("embedding") String embedding, @Param("limit") int limit);

    @Query(value = """
            select product_id
            from support.product_embedding
            where status = 'ACTIVE'
              and product_id not in (:excludedProductIds)
            order by embedding <-> CAST(:embedding AS vector)
            limit :limit
            """, nativeQuery = true)
    List<UUID> findSimilarActiveProductIdsExcluding(
            @Param("embedding") String embedding,
            @Param("excludedProductIds") List<UUID> excludedProductIds,
            @Param("limit") int limit
    );
}
