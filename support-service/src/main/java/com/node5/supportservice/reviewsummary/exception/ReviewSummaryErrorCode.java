package com.node5.supportservice.reviewsummary.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewSummaryErrorCode implements BaseErrorCode {
    OPENAI_API_KEY_MISSING(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_001", "OpenAI API Key가 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
