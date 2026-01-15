package com.node5.supportservice.reviewsummary.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewSummaryErrorCode implements BaseErrorCode {
    OPENAI_API_KEY_MISSING(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_001", "OpenAI API Key가 없습니다."),
    OPENAI_REQUEST_FAILED(HttpStatus.SERVICE_UNAVAILABLE.value(), "REVIEW_SUMMARY_002", "OpenAI 요청에 실패했습니다."),
    OPENAI_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY.value(), "REVIEW_SUMMARY_003", "OpenAI 응답이 비어 있습니다."),
    PROMPT_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_004", "프롬프트 직렬화에 실패했습니다."),
    OPENAI_EMBEDDING_MODEL_MISSING(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_005", "OpenAI 임베딩 모델이 없습니다."),
    OPENAI_EMBEDDING_REQUEST_FAILED(HttpStatus.SERVICE_UNAVAILABLE.value(), "REVIEW_SUMMARY_006", "OpenAI 임베딩 요청에 실패했습니다."),
    OPENAI_EMBEDDING_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY.value(), "REVIEW_SUMMARY_007", "OpenAI 임베딩 응답이 비어 있습니다.");

    private final int status;
    private final String code;
    private final String message;
}
