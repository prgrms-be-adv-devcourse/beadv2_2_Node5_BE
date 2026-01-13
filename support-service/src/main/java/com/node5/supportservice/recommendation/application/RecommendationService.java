package com.node5.supportservice.recommendation.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.supportservice.recommendation.application.dto.PromptPayload;
import com.node5.supportservice.recommendation.client.dto.ProductIdsRequest;
import com.node5.supportservice.recommendation.client.dto.ProductSummaryListResponse;
import com.node5.supportservice.recommendation.client.openfeign.OrderClient;
import com.node5.supportservice.recommendation.client.openfeign.ProductClient;
import com.node5.supportservice.recommendation.exception.RecommendationErrorCode;
import com.node5.supportservice.recommendation.exception.RecommendationException;
import com.node5.supportservice.recommendation.client.openai.ChatClient;
import com.node5.supportservice.recommendation.client.openai.EmbeddingClient;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private static final String SYSTEM_PROMPT =
            "너는 반드시 요청된 형식을 지키며 1문장만 출력한다. 추가 설명이나 목록은 금지한다.";

    private final ChatClient chatClient;
    private final EmbeddingClient embeddingClient;
    private final ObjectMapper objectMapper;
    private final ProductClient productClient;
    private final OrderClient orderClient;

    public Result recommend(UUID memberId, List<UUID> cartItemIds) {
        // 장바구니 내역
        ProductSummaryListResponse cartItemResponse = getProductInfo(memberId, cartItemIds, "장바구니");
        log.info("조회된 장바구니 내역 size: {}", cartItemResponse.products().size());

        // 주문 내역
        List<UUID> recentOrderIds = getRecentOrderIds(memberId);
        ProductSummaryListResponse orderItemResponse = getProductInfo(memberId, recentOrderIds, "주문");
        log.info("조회된 주문 내역 size: {}", orderItemResponse.products().size());

        // LLM
        String prompt = createPrompt(orderItemResponse.products(), cartItemResponse.products());
        String tasteSummary = chatClient.generateRecommendation(prompt, SYSTEM_PROMPT);

        // Embedding
        List<Double> embedding = embeddingClient.embed(tasteSummary);
        log.info("** LLM TASTE SUMMARY: {}", tasteSummary);
        log.info("** LLM TASTE EMBEDDING: {}", embedding);

        return new Result(tasteSummary, embedding);
    }

    private ProductSummaryListResponse getProductInfo(UUID memberId, List<UUID> ids, String context) {
        if (ids == null || ids.isEmpty()) {
            return new ProductSummaryListResponse(Collections.emptyList());
        }

        try {
            ResponseEntity<ProductSummaryListResponse> response =
                    productClient.getProductsByIds(memberId, ProductIdsRequest.from(ids));

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
        return "cartItems 70%, orderItems 30% 비중으로 반영해줘."
                + "장바구니, 주문 내역에서 공통으로 반복되는 속성(카테고리/기능/용도/성향)을 우선 사용해줘."
                + "형식: \"사용자는 최근 [주요 관심사 1~2개]와 [보조 관심사 1~2개]에 관심이 높고, "
                + "[주문 내역 기반 생활 패턴 1~2개]을 보인다. 특히 [장바구니 기반 핵심 속성 1~2개]을 선호한다.\"";
    }

    public record Result(String tasteSummary, List<Double> embedding) {}
}
