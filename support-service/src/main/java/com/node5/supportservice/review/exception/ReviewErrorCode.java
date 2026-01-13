package com.node5.supportservice.review.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReviewErrorCode implements BaseErrorCode {
    REVIEW_UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "REVIEW_001", "리뷰에 대한 권한이 없습니다."),
    REVIEW_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "REVIEW_002" , "상품이 존재하지 않습니다."),
    MEMBER_NICKNAME_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "REVIEW_003", "존재하지 않는 회원이거나 회원닉네임이 없습니다."),
    MEMBER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value(), "REVIEW_004" , "MEMBER_SERVICE_UNAVAILABLE"),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "REVIEW_005", "리뷰를 찾을 수 없습니다."),
    REVIEW_ALREADY_LIKED(HttpStatus.BAD_REQUEST.value(), "REVIEW_006", "이미 공감한 리뷰입니다"),
    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "REVIEW_007", "이미 작성된 리뷰가 존재합니다."),
    REVIEW_CANNOT_LIKE_OWN(HttpStatus.BAD_REQUEST.value(), "REVIEW_008", "본인의 리뷰는 공감할 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;

    ReviewErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
