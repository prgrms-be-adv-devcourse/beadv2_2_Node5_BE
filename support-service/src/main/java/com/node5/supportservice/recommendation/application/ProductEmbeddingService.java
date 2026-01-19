package com.node5.supportservice.recommendation.application;

import com.node5.common.event.ProductEmbeddingEvent;
import com.node5.supportservice.global.openfeign.client.CatalogClient;
import com.node5.supportservice.global.openfeign.client.dto.ProductIdsRequest;
import com.node5.supportservice.global.openfeign.client.dto.ProductSummaryListResponse;
import com.node5.supportservice.recommendation.client.openai.EmbeddingClient;
import com.node5.supportservice.recommendation.domain.ProductEmbedding;
import com.node5.supportservice.recommendation.domain.ProductEmbeddingRepository;
import com.node5.supportservice.recommendation.domain.ProductEmbeddingStatus;
import com.node5.supportservice.recommendation.exception.RecommendationErrorCode;
import com.node5.supportservice.recommendation.exception.RecommendationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductEmbeddingService {

    private final ProductEmbeddingRepository productEmbeddingRepository;
    private final EmbeddingClient embeddingClient;
    private final CatalogClient catalogClient;

    public void handleProductEmbeddingEvent(ProductEmbeddingEvent event) {
        ProductEmbeddingStatus status = resolveEventStatus(event.status());
        if (status == ProductEmbeddingStatus.DELETED) {
            deleteEmbedding(event.productId());
            return;
        }
        upsertEmbedding(event.productId(), event.name(), event.description(), event.category(), status);
    }

    // 컨트롤러 api 요청 시 연결되는 upsert 메소드
    public void upsertEmbedding(UUID productId, String name, String description, String category, String productStatus) {
        ProductEmbeddingStatus status = resolveEventStatus(productStatus);
        upsertEmbedding(productId, name, description, category, status);
    }

    // 이벤트 수신 시 연결되는 upsert 메소드
    public void upsertEmbedding(UUID productId, String name, String description, String category, ProductEmbeddingStatus status) {
        String content = buildContent(name, description, category);
        validateContent(content);
        float[] embedding = embeddingClient.embed(content);

        ProductEmbedding embeddingEntity = ProductEmbedding.create(productId, content, status, embedding);
        productEmbeddingRepository.save(embeddingEntity);
    }

    public void deleteEmbedding(UUID productId) {
        productEmbeddingRepository.markDeletedByProductId(productId);
    }

    public BackfillResult backfillEmbeddings(UUID memberId, int pageSize, Integer maxPages) {
        int page = 0;
        int processed = 0;
        int resolvedPageSize = pageSize < 1 ? 200 : pageSize;

        while (true) {
            if (maxPages != null && maxPages > 0 && page >= maxPages) {
                break;
            }

            PageRequest pageRequest = PageRequest.of(page, resolvedPageSize);
            List<UUID> productIds = getProductIds(pageRequest);
            if (productIds.isEmpty()) {
                break;
            }

            ProductSummaryListResponse summaries = getProductSummaries(memberId, productIds);
            for (ProductSummaryListResponse.ProductSummaryResponse summary : summaries.products()) {
                try {
                    upsertEmbedding(
                            summary.productId(),
                            summary.name(),
                            summary.description(),
                            summary.category(),
                            ProductEmbeddingStatus.ACTIVE
                    );
                    processed++;
                } catch (Exception e) {
                    log.warn("ProductEmbedding backfill failed (productId: {}): {}", summary.productId(), e.getMessage());
                }
            }
            page++;
        }

        return new BackfillResult(processed, page, resolvedPageSize);
    }

    private List<UUID> getProductIds(PageRequest pageRequest) {
        try {
            ResponseEntity<List<UUID>> response = catalogClient.getProductIds(pageRequest);
            return response != null && response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            log.error("ProductEmbedding backfill failed to fetch product ids: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private ProductSummaryListResponse getProductSummaries(UUID memberId, List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ProductSummaryListResponse(Collections.emptyList());
        }

        try {
            ResponseEntity<ProductSummaryListResponse> response =
                    catalogClient.getProductsByIds(memberId, ProductIdsRequest.from(ids));
            return response != null && response.getBody() != null
                    ? response.getBody()
                    : new ProductSummaryListResponse(Collections.emptyList());
        } catch (Exception e) {
            log.error("ProductEmbedding backfill failed to fetch product summaries: {}", e.getMessage());
            return new ProductSummaryListResponse(Collections.emptyList());
        }
    }

    private String buildContent(String name, String description, String category) {
        String safeName = name == null ? "" : name;
        String safeDescription = description == null ? "" : description;
        String safeCategory = category == null ? "" : category;
        return String.format("%s %s %s", safeName, safeCategory, safeDescription);
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new RecommendationException(RecommendationErrorCode.PRODUCT_EMBEDDING_CONTENT_EMPTY);
        }
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

    public record BackfillResult(int processed, int pages, int pageSize) {}
}
