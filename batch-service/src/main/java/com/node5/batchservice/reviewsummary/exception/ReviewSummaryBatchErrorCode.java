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
    JOB_IS_RUNNING(HttpStatus.CONFLICT.value(), "REVIEW_SUMMARY_008", "Job일 실행 중 입니다."),
    JOB_LAUNCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_009", "리뷰 요약 배치 실행에 실패했습니다."),
    JOB_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST.value(), "REVIEW_SUMMARY_010", "이미 완료된 배치 실행입니다."),
    JOB_EXECUTION_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "REVIEW_SUMMARY_011", "해당 배치 실행 이력을 찾을 수 없습니다."),
    JOB_NOT_RESTARTABLE(HttpStatus.BAD_REQUEST.value(), "REVIEW_SUMMARY_013", "해당 배치 실행은 재시작할 수 없는 상태입니다."),
    JOB_RESTART_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_014", "배치 재시작에 실패했습니다."),
    JOB_PARAMETERS_INVALID(HttpStatus.BAD_REQUEST.value(), "REVIEW_SUMMARY_015", "배치 파라미터가 유효하지 않습니다."),
    GET_REVIEW_SUMMARY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_016", "리뷰 요약을 불러오지 못했습니다."),
    GET_REVIEWS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_017", "리뷰를 불러오지 못했습니다."),
    SUMMARY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SUMMARY_018", "리뷰 요약에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
