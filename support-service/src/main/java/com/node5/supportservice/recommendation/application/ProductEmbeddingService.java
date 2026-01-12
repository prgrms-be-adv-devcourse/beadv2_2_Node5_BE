package com.node5.supportservice.recommendation.application;

import com.node5.supportservice.recommendation.domain.ProductEmbedding;
import com.node5.supportservice.recommendation.domain.ProductEmbeddingRepository;
import com.node5.supportservice.recommendation.domain.ProductEmbeddingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductEmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final ProductEmbeddingRepository productEmbeddingRepository;

    public void upsertEmbedding(UUID productId, String name, String description, String category, String productStatus) {
        String content = buildContent(name, description, category);
        float[] embedding = embeddingModel.embed(content);
        ProductEmbeddingStatus status = resolveStatus(productStatus);

        ProductEmbedding embeddingEntity = productEmbeddingRepository.findByProductId(productId)
                .map(existing -> {
                    existing.update(content, status, embedding);
                    return existing;
                })
                .orElseGet(() -> ProductEmbedding.create(productId, content, status, embedding));

        productEmbeddingRepository.save(embeddingEntity);
    }

    public void deleteEmbedding(UUID productId) {
        productEmbeddingRepository.findByProductId(productId)
                .ifPresent(embedding -> {
                    embedding.markDeleted();
                    productEmbeddingRepository.save(embedding);
                });
    }

    private String buildContent(String name, String description, String category) {
        String safeName = name == null ? "" : name;
        String safeDescription = description == null ? "" : description;
        String safeCategory = category == null ? "" : category;
        return String.format("%s %s %s", safeName, safeCategory, safeDescription);
    }

    private ProductEmbeddingStatus resolveStatus(String productStatus) {
        if (productStatus == null) {
            return ProductEmbeddingStatus.INACTIVE;
        }
        return switch (productStatus) {
            case "ON_SALE" -> ProductEmbeddingStatus.ACTIVE;
            case "HIDDEN", "DISCONTINUED" -> ProductEmbeddingStatus.INACTIVE;
            default -> ProductEmbeddingStatus.INACTIVE;
        };
    }
}
