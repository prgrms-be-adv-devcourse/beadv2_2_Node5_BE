package com.node5.supportservice.recommendation.domain;

import com.node5.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_embedding", schema = "support")
public class ProductEmbedding extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "product_id", nullable = false, unique = true)
    private UUID productId;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductEmbeddingStatus status;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "vector(1536)", nullable = false)
    private float[] embedding;

    private ProductEmbedding(UUID id, UUID productId, String content, ProductEmbeddingStatus status, float[] embedding) {
        this.id = id;
        this.productId = productId;
        this.content = content;
        this.status = status;
        this.embedding = embedding;
    }

    public static ProductEmbedding create(UUID productId, String content, ProductEmbeddingStatus status, float[] embedding) {
        return new ProductEmbedding(UUID.randomUUID(), productId, content, status, embedding);
    }

    public void update(String content, ProductEmbeddingStatus status, float[] embedding) {
        this.content = content;
        this.status = status;
        this.embedding = embedding;
    }

    public void markDeleted() {
        this.status = ProductEmbeddingStatus.DELETED;
    }
}