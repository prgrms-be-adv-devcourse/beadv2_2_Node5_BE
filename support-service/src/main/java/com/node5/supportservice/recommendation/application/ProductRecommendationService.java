package com.node5.supportservice.recommendation.application;

import com.node5.supportservice.recommendation.application.dto.ProductRecommendationInfo;
import com.node5.supportservice.recommendation.domain.ProductEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductRecommendationService {

    private static final int DEFAULT_LIMIT = 10;

    private final ProductEmbeddingRepository productEmbeddingRepository;

    public ProductRecommendationInfo recommendProducts(float[] preferenceEmbedding, List<UUID> excludedProductIds, Integer limit) {
        if (preferenceEmbedding == null || preferenceEmbedding.length == 0) {
            return ProductRecommendationInfo.of(Collections.emptyList());
        }

        List<UUID> recommendList = new ArrayList<>();
        int resolvedLimit = resolveLimit(limit);
        if (excludedProductIds == null || excludedProductIds.isEmpty()) {
            recommendList = productEmbeddingRepository.findSimilarActiveProductIds(preferenceEmbedding, resolvedLimit);
        } else {
            recommendList = productEmbeddingRepository.findSimilarActiveProductIdsExcluding(preferenceEmbedding, excludedProductIds, resolvedLimit);
        }
        return ProductRecommendationInfo.of(recommendList);
    }

    private int resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return limit;
    }
}
