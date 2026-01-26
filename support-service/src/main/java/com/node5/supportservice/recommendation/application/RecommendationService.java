package com.node5.supportservice.recommendation.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.supportservice.chat.ChatService;
import com.node5.supportservice.recommendation.application.dto.ProductRecommendationInfo;
import com.node5.supportservice.recommendation.application.dto.PromptPayload;
import com.node5.supportservice.global.openfeign.client.dto.ProductIdsRequest;
import com.node5.supportservice.global.openfeign.client.dto.ProductSummaryListResponse;
import com.node5.supportservice.global.openfeign.client.OrderClient;
import com.node5.supportservice.global.openfeign.client.CatalogClient;
import com.node5.supportservice.recommendation.domain.ProductEmbeddingRepository;
import com.node5.supportservice.recommendation.exception.RecommendationErrorCode;
import com.node5.supportservice.recommendation.exception.RecommendationException;
import com.node5.supportservice.recommendation.client.openai.EmbeddingClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.time.Duration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private static final String SYSTEM_PROMPT =
            "너는 반드시 요청된 형식을 지키며 1문장만 출력한다. 추가 설명이나 목록은 금지한다.";

    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 10;
    private static final Duration RECOMMENDATION_CACHE_TTL = Duration.ofMinutes(30);
    private static final String CACHE_KEY_PREFIX = "recommendation:taste:";

    private final ProductEmbeddingRepository productEmbeddingRepository;
    private final ChatService chatService;
    private final EmbeddingClient embeddingClient;
    private final ObjectMapper objectMapper;
    private final CatalogClient catalogClient;
    private final OrderClient orderClient;
    private final StringRedisTemplate stringRedisTemplate;

    // 장바구니 아이템 받아서 취향 임베딩 반환
    public Result recommendTaste(UUID memberId, List<UUID> cartItemProductIds) {
        Result cachedResult = getCachedTaste(memberId, cartItemProductIds);
        if (cachedResult != null) {
            return cachedResult;
        }

        // 장바구니 내역
        ProductSummaryListResponse cartItemResponse = getProductInfo(memberId, cartItemProductIds, "장바구니");
        log.info("조회된 장바구니 내역 size: {}", cartItemResponse.products().size());

        // 주문 내역
        List<UUID> recentOrderProductIds = getRecentOrderIds(memberId);
        ProductSummaryListResponse orderItemResponse = getProductInfo(memberId, recentOrderProductIds, "주문");
        log.info("조회된 주문 내역 size: {}", orderItemResponse.products().size());

        // 장바구니 내역, 주문 내역에 포함되어 있는 상품 리스트
        List<UUID> existedProductIds = getExistedProductIds(cartItemProductIds, recentOrderProductIds);

        // LLM
        String userPrompt = createPrompt(orderItemResponse.products(), cartItemResponse.products());
        String tasteSummary = chatService.callLlm(SYSTEM_PROMPT, userPrompt, "Recommendation");

        // Embedding
        float[] embedding = embeddingClient.embed(tasteSummary);
        log.info("** LLM TASTE SUMMARY: {}", tasteSummary);
        log.info("** LLM TASTE EMBEDDING: {}", Arrays.toString(embedding));

        Result result = new Result(tasteSummary, embedding, existedProductIds);
        cacheTaste(memberId, cartItemProductIds, result);
        return result;
    }

    private List<UUID> getExistedProductIds(List<UUID> cartItemProductIds, List<UUID> recentOrderProductIds) {
        LinkedHashSet<UUID> merged = new LinkedHashSet<>();
        if (cartItemProductIds != null) {
            merged.addAll(cartItemProductIds);
        }
        if (recentOrderProductIds != null) {
            merged.addAll(recentOrderProductIds);
        }
        return new ArrayList<>(merged);
    }

    // 취향 임베딩 입력받아서 추천 상품 리스트 반환
    public ProductRecommendationInfo recommendProducts(UUID memberId, List<UUID> cartItemProductIds, Integer limit) {
        Result result = recommendTaste(memberId, cartItemProductIds);

        float[] preferenceEmbedding = result.embedding();
        List<UUID> excludedProductIds = result.existedProductIds;

        if (preferenceEmbedding == null || preferenceEmbedding.length == 0) {
            throw new RecommendationException(RecommendationErrorCode.OPENAI_EMBEDDING_RESPONSE_EMPTY);
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

    private ProductSummaryListResponse getProductInfo(UUID memberId, List<UUID> ids, String context) {
        if (ids == null || ids.isEmpty()) {
            return new ProductSummaryListResponse(Collections.emptyList());
        }

        try {
            ResponseEntity<ProductSummaryListResponse> response =
                    catalogClient.getProductsByIds(memberId, ProductIdsRequest.from(ids));

            return (response != null && response.getBody() != null)
                    ? response.getBody()
                    : new ProductSummaryListResponse(Collections.emptyList());

        } catch (Exception e) {
            log.error("[API 에러] {} 내역 상품 정보 조회 실패 (memberId: {}): {}", context, memberId, e.getMessage());
            return new ProductSummaryListResponse(Collections.emptyList());
        }
    }

    private List<UUID> getRecentOrderIds(UUID memberId) {
        try {
            ResponseEntity<List<UUID>> response = orderClient.getRecentOrderList(memberId);

            return (response != null && response.getBody() != null)
                    ? response.getBody()
                    : Collections.emptyList();

        } catch (Exception e) {
            log.error("[API 에러] 최근 주문 목록 조회 실패 (memberId: {}): {}", memberId, e.getMessage());
            return Collections.emptyList();
        }
    }

    private String createPrompt(
            List<ProductSummaryListResponse.ProductSummaryResponse> orderItems,
            List<ProductSummaryListResponse.ProductSummaryResponse> cartItems
    ) {
        String prompt = promptTemplate();

        PromptPayload payload = new PromptPayload(
                prompt,
                PromptPayload.fromItems(orderItems),
                PromptPayload.fromItems(cartItems)
        );

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new RecommendationException(RecommendationErrorCode.PROMPT_SERIALIZATION_FAILED);
        }
    }

    private String promptTemplate() {
        return "cartItems 60%, orderItems 40% 비중으로 반영하되(둘 중 하나만 있을 경우엔 해당 데이터만 100% 활용)"
                + "장바구니, 주문 내역에서 공통으로 반복되는 속성(카테고리/기능/용도/성향)을 우선 사용해줘."
                + "데이터가 없을 경우, 최근 2030 인기 라이프스타일 기반의 대중적인 취향으로 생성해줘."
                + "응답 형식: \"사용자는 최근 [주요 관심사 1~2개]와 [보조 관심사 1~2개]에 관심이 높고, "
                + "[생활 패턴 1~2개]을 보인다. 특히 [핵심 선호 속성 1~2개]을 선호한다.\"";
    }

    public record Result(String tasteSummary, float[] embedding, List<UUID> existedProductIds) {}

    private int resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        if (limit > MAX_LIMIT) {
            throw new RecommendationException(RecommendationErrorCode.RECOMMENDATION_LIMIT_TOO_HIGH);
        }
        return limit;
    }

    private Result getCachedTaste(UUID memberId, List<UUID> cartItemProductIds) {
        String cacheKey = buildCacheKey(memberId, cartItemProductIds);
        try {
            String cachedJson = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cachedJson == null || cachedJson.isBlank()) {
                return null;
            }
            CachedTaste cachedTaste = objectMapper.readValue(cachedJson, CachedTaste.class);
            return new Result(cachedTaste.tasteSummary(), cachedTaste.embedding(), cachedTaste.existedProductIds());
        } catch (Exception ex) {
            log.warn("추천 캐시 조회 실패 (key: {}): {}", cacheKey, ex.getMessage());
            return null;
        }
    }

    private void cacheTaste(UUID memberId, List<UUID> cartItemProductIds, Result result) {
        String cacheKey = buildCacheKey(memberId, cartItemProductIds);
        try {
            CachedTaste cachedTaste = new CachedTaste(result.tasteSummary(), result.embedding(), result.existedProductIds());
            String cachedJson = objectMapper.writeValueAsString(cachedTaste);
            stringRedisTemplate.opsForValue().set(cacheKey, cachedJson, RECOMMENDATION_CACHE_TTL);
        } catch (Exception ex) {
            log.warn("추천 캐시 저장 실패 (key: {}): {}", cacheKey, ex.getMessage());
        }
    }

    private String buildCacheKey(UUID memberId, List<UUID> cartItemProductIds) {
        List<UUID> sortedIds = new ArrayList<>();
        if (cartItemProductIds != null) {
            sortedIds.addAll(cartItemProductIds);
        }
        sortedIds.sort(Comparator.comparing(UUID::toString));
        String rawKey = memberId + ":" + sortedIds;
        return CACHE_KEY_PREFIX + sha256(rawKey);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException ex) {
            log.warn("SHA-256 not available. Falling back to raw cache key.");
            return value;
        }
    }

    private record CachedTaste(String tasteSummary, float[] embedding, List<UUID> existedProductIds) {}
}
