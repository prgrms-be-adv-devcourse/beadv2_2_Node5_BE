package com.node5.memberservice.inquiry.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum InquiryErrorCode implements BaseErrorCode {
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "INQUIRY_001", "존재하지 않는 문의입니다."),
    INQUIRY_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "INQUIRY_002", "잘못된 접근입니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST.value(), "INQUIRY_003", "잘못된 카테고리입니다."),
    INQUIRY_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST.value(), "INQUIRY_004", "문의가 답변 중이거나 답변이 완료된 상태입니다."),
    INQUIRY_ALREADY_ANSWERED(HttpStatus.BAD_REQUEST.value(), "INQUIRY_005", "문의가 답변이 완료된 상태입니다.");

    private final int status;
    private final String code;
    private final String message;

    InquiryErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
