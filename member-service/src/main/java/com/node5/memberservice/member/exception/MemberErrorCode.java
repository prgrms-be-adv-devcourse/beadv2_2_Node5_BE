package com.node5.memberservice.member.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode implements BaseErrorCode {
    INVALID_ROLE(HttpStatus.BAD_REQUEST.value(), "MEMBER_001", "잘못된 role 값입니다."),
    INVALID_ACTION(HttpStatus.BAD_REQUEST.value(),  "MEMBER_002", "잘못된 action 값입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "MEMBER_003", "존재하지 않는 회원입니다."),
    INVALID_STATUS(HttpStatus.BAD_REQUEST.value(), "MEMBER_004", "잘못된 status 값입니다."),
    DELETED_MEMBER_CANNOT_BE_MODIFIED(HttpStatus.BAD_REQUEST.value(), "MEMBER_005", "삭제된 회원은 수정할 수 없습니다."),
    CANNOT_MODIFY_SELF(HttpStatus.BAD_REQUEST.value(), "MEMBER_006", "자기 자신의 상태는 수정할 수 없습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "MEMBER_007", "서버 에러"),
    MEMBER_HAS_BALANCE(HttpStatus.BAD_REQUEST.value(), "MEMBER_008", "예치금 잔액이 남아있습니다. 예치금 환불 후 탈퇴해 주세요."),
    BILLING_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value(), "MEMBER_009",  "예치금 정보를 확인할 수 없습니다. 잠시 후 다시 시도해 주세요.");

    private final int status;
    private final String code;
    private final String message;

    MemberErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
