package com.node5.supportservice.recommendation.infrastructure;

import com.node5.supportservice.recommendation.domain.ProductEmbedding;
import com.node5.supportservice.recommendation.domain.ProductEmbeddingRepository;
import com.node5.supportservice.recommendation.domain.ProductEmbeddingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;


import java.sql.Types;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductEmbeddingRepositoryAdapter implements ProductEmbeddingRepository {

    private final ProductEmbeddingJpaRepository productEmbeddingJpaRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String UPSERT_SQL = """
            INSERT INTO support.product_embedding
            (id, product_id, content, status, embedding, created_at, modified_at)
            VALUES (:id, :productId, :content, :status, CAST(:embedding AS vector), now(), now())
            ON CONFLICT (product_id) DO UPDATE
            SET content = EXCLUDED.content,
                status = EXCLUDED.status,
                embedding = EXCLUDED.embedding,
                modified_at = now()
            """;

    private static final String MARK_DELETED_SQL = """
            UPDATE support.product_embedding
            SET status = :status,
                modified_at = now()
            WHERE product_id = :productId
            """;

    @Override
    public ProductEmbedding save(ProductEmbedding productEmbedding) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", productEmbedding.getId())
                .addValue("productId", productEmbedding.getProductId())
                .addValue("content", productEmbedding.getContent())
                .addValue("status", productEmbedding.getStatus().name())
                .addValue("embedding", new SqlParameterValue(Types.OTHER, toVectorLiteral(productEmbedding.getEmbedding())));
        namedParameterJdbcTemplate.update(UPSERT_SQL, params);
        return productEmbedding;
    }

    @Override
    public void markDeletedByProductId(UUID productId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("productId", productId)
                .addValue("status", ProductEmbeddingStatus.DELETED.name());
        namedParameterJdbcTemplate.update(MARK_DELETED_SQL, params);
    }

    @Override
    public List<UUID> findSimilarActiveProductIds(float[] preferenceEmbedding, int limit) {
        String embeddingLiteral = toVectorLiteral(preferenceEmbedding);
        return productEmbeddingJpaRepository.findSimilarActiveProductIds(embeddingLiteral, limit);
    }

    @Override
    public List<UUID> findSimilarActiveProductIdsExcluding(float[] preferenceEmbedding, List<UUID> excludedProductIds, int limit) {
        String embeddingLiteral = toVectorLiteral(preferenceEmbedding);
        return productEmbeddingJpaRepository.findSimilarActiveProductIdsExcluding(
                embeddingLiteral,
                excludedProductIds,
                limit
        );
    }

    private static String toVectorLiteral(float[] embedding) {
        if (embedding == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(embedding[i]);
        }
        builder.append(']');
        return builder.toString();
    }
}
