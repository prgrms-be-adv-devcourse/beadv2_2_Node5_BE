package com.node5.memberservice.member.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode implements BaseErrorCode {
    INVALID_ROLE(HttpStatus.BAD_REQUEST.value(), "MEMBER_001", "잘못된 role 값입니다."),
    INVALID_ACTION(HttpStatus.BAD_REQUEST.value(),  "MEMBER_002", "잘못된 action 값입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "MEMBER_003", "존재하지 않는 회원입니다.");

    private final int status;
    private final String code;
    private final String message;

    MemberErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
