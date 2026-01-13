package com.node5.supportservice.recommendation.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.supportservice.recommendation.application.dto.PromptPayload;
import com.node5.supportservice.recommendation.exception.RecommendationErrorCode;
import com.node5.supportservice.recommendation.exception.RecommendationException;
import com.node5.supportservice.recommendation.client.openai.ChatClient;
import com.node5.supportservice.recommendation.client.openai.EmbeddingClient;
import com.node5.supportservice.recommendation.presentation.dto.RecommendationRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public Result recommend(
        List<RecommendationRequest.ProductItem> orderItems,
        List<RecommendationRequest.ProductItem> cartItems
    ) {
        String prompt = createPrompt(orderItems, cartItems);
        String tasteSummary = chatClient.generateRecommendation(prompt, SYSTEM_PROMPT);
        List<Double> embedding = embeddingClient.embed(tasteSummary);
        log.info("** LLM TASTE SUMMARY: {}", tasteSummary);
        log.info("** LLM TASTE EMBEDDING: {}", embedding);
        return new Result(tasteSummary, embedding);
    }

    private String createPrompt(
        List<RecommendationRequest.ProductItem> orderItems,
        List<RecommendationRequest.ProductItem> cartItems
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

//    private String promptTemplate() {
//        return "cartItems 70%, orderItems 30% 비중으로 반영해 주세요. "
//            + "장바구니에서 공통으로 반복되는 속성(카테고리/기능/용도/성향)을 우선 사용하고, "
//            + "주문 내역은 생활 패턴의 보조 근거로만 사용해 주세요. "
//            + "상품명/브랜드 같은 고유명사는 쓰지 말고 속성/카테고리/용도만 사용해 주세요. "
//            + "아래 형식을 지켜 1문장만 작성해 주세요. "
//            + "형식: \"사용자는 최근 [주요 관심사 1~2개]와 [보조 관심사 1~2개]에 관심이 높고, "
//            + "[주문 내역 기반 생활 패턴 1~2개]을 보인다. 특히 [장바구니 기반 핵심 속성 1~2개]을 선호한다.\"";
//    }
//
//    private String promptTemplate() {
//        return "cartItems 70%, orderItems 30% 비중으로 반영해줘. "
//                + "장바구니에서 공통으로 반복되는 속성(카테고리/기능/용도/성향)을 우선 사용하고, "
//                + "주문 내역은 생활 패턴의 보조 근거로만 사용해줘."
//                + "형식: \"사용자는 최근 [주요 관심사 1~2개]와 [보조 관심사 1~2개]에 관심이 높고, "
//                + "[주문 내역 기반 생활 패턴 1~2개]을 보인다. 특히 [장바구니 기반 핵심 속성 1~2개]을 선호한다.\"";
//    }

    private String promptTemplate() {
        return "cartItems 70%, orderItems 30% 비중으로 반영해줘."
                + "장바구니, 주문 내역에서 공통으로 반복되는 속성(카테고리/기능/용도/성향)을 우선 사용해줘."
                + "형식: \"사용자는 최근 [주요 관심사 1~2개]와 [보조 관심사 1~2개]에 관심이 높고, "
                + "[주문 내역 기반 생활 패턴 1~2개]을 보인다. 특히 [장바구니 기반 핵심 속성 1~2개]을 선호한다.\"";
    }

    public record Result(String tasteSummary, List<Double> embedding) {}
}
