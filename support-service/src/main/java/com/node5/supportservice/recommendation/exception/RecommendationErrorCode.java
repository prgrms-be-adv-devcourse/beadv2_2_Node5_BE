package com.node5.supportservice.recommendation.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecommendationErrorCode implements BaseErrorCode {
    OPENAI_API_KEY_MISSING(HttpStatus.INTERNAL_SERVER_ERROR.value(), "RECOMMENDATION_001", "OpenAI API Key가 없습니다."),
    OPENAI_REQUEST_FAILED(HttpStatus.SERVICE_UNAVAILABLE.value(), "RECOMMENDATION_002", "OpenAI 요청에 실패했습니다."),
    OPENAI_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY.value(), "RECOMMENDATION_003", "OpenAI 응답이 비어 있습니다."),
    PROMPT_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "RECOMMENDATION_004", "프롬프트 직렬화에 실패했습니다."),
    OPENAI_EMBEDDING_MODEL_MISSING(HttpStatus.INTERNAL_SERVER_ERROR.value(), "RECOMMENDATION_005", "OpenAI 임베딩 모델이 없습니다."),
    OPENAI_EMBEDDING_REQUEST_FAILED(HttpStatus.SERVICE_UNAVAILABLE.value(), "RECOMMENDATION_006", "OpenAI 임베딩 요청에 실패했습니다."),
    OPENAI_EMBEDDING_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY.value(), "RECOMMENDATION_007", "OpenAI 임베딩 응답이 비어 있습니다."),
    PRODUCT_SERVICE_REQUEST_FAILED(HttpStatus.SERVICE_UNAVAILABLE.value(), "RECOMMENDATION_008", "상품 서비스 요청에 실패했습니다."),
    ORDER_SERVICE_REQUEST_FAILED(HttpStatus.SERVICE_UNAVAILABLE.value(), "RECOMMENDATION_009", "주문 서비스 요청에 실패했습니다."),
    PRODUCT_EMBEDDING_CONTENT_EMPTY(HttpStatus.BAD_REQUEST.value(), "RECOMMENDATION_010", "상품 임베딩을 위한 컨텐츠가 비어 있습니다."),
    RECOMMENDATION_LIMIT_TOO_HIGH(HttpStatus.BAD_REQUEST.value(), "RECOMMENDATION_011", "상품 추천 limit이 너무 높습니다.");

    private final int status;
    private final String code;
    private final String message;
}
