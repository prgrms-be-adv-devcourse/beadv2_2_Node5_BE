package com.node5.batchservice.reviewsummary.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewSummaryBatchErrorCode implements BaseErrorCode {
    OPENAI_REQUEST_FAILED(HttpStatus.SERVICE_UNAVAILABLE.value(), "REVIEW_SUMMARY_002", "OpenAI 요청에 실패했습니다."),
    OPENAI_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY.value(), "REVIEW_SUMMARY_003", "OpenAI 응답이 비어 있습니다."),
    GET_REVIEW_SUMMARY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_016", "리뷰 요약을 불러오지 못했습니다."),
    GET_REVIEWS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_017", "리뷰를 불러오지 못했습니다."),
    SUMMARY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_018", "리뷰 요약에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
