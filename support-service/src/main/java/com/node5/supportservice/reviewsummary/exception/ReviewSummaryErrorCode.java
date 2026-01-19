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
    OPENAI_EMBEDDING_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY.value(), "REVIEW_SUMMARY_007", "OpenAI 임베딩 응답이 비어 있습니다."),
    JOB_IS_RUNNING(HttpStatus.CONFLICT.value(), "REVIEW_SUMMARY_008", "Job일 실행 중 입니다."),
    JOB_LAUNCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_009", "리뷰 요약 배치 실행에 실패했습니다."),
    JOB_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST.value(), "REVIEW_SUMMARY_010", "이미 완료된 배치 실행입니다."),
    JOB_EXECUTION_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "REVIEW_SUMMARY_011", "해당 배치 실행 이력을 찾을 수 없습니다."),
    JOB_NOT_RESTARTABLE(HttpStatus.BAD_REQUEST.value(), "REVIEW_SUMMARY_013", "해당 배치 실행은 재시작할 수 없는 상태입니다."),
    JOB_RESTART_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_014", "배치 재시작에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
