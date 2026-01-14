package com.node5.supportservice.recommendation.application;

import com.node5.common.event.ProductEmbeddingEvent;
import com.node5.supportservice.recommendation.client.openai.EmbeddingClient;
import com.node5.supportservice.recommendation.domain.ProductEmbedding;
import com.node5.supportservice.recommendation.domain.ProductEmbeddingRepository;
import com.node5.supportservice.recommendation.domain.ProductEmbeddingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductEmbeddingService {

    private final ProductEmbeddingRepository productEmbeddingRepository;
    private final EmbeddingClient embeddingClient;

    public void handleProductEmbeddingEvent(ProductEmbeddingEvent event) {
        ProductEmbeddingStatus status = resolveEventStatus(event.status());
        if (status == ProductEmbeddingStatus.DELETED) {
            deleteEmbedding(event.productId());
            return;
        }
        upsertEmbedding(event.productId(), event.name(), event.description(), event.category(), status);
    }

    public void upsertEmbedding(UUID productId, String name, String description, String category, ProductEmbeddingStatus status) {
        String content = buildContent(name, description, category);
        float[] embedding = embeddingClient.embed(content);

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

    private ProductEmbeddingStatus resolveEventStatus(String productStatus) {
        if (productStatus == null) {
            log.warn("ProductEmbeddingEvent status is null; defaulting to INACTIVE");
            return ProductEmbeddingStatus.INACTIVE;
        }
        return switch (productStatus) {
            case "ON_SALE" -> ProductEmbeddingStatus.ACTIVE;
            case "HIDDEN" -> ProductEmbeddingStatus.INACTIVE;
            case "DISCONTINUED" -> ProductEmbeddingStatus.DELETED;
            default -> {
                log.warn("Unknown ProductEmbeddingEvent status: {}; defaulting to INACTIVE", productStatus);
                yield ProductEmbeddingStatus.INACTIVE;
            }
        };
    }
}
