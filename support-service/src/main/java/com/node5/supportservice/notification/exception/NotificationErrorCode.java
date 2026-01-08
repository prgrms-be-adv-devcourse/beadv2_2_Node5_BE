package com.node5.supportservice.notification.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NotificationErrorCode implements BaseErrorCode {
    MEMBER_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "NOTIFICATION_001", "존재하지 않는 회원이거나 회원이메일이 없습니다."),
    MEMBER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value(), "NOTIFICATION_002" , "MEMBER_SERVICE_UNAVAILABLE");

    private final int status;
    private final String code;
    private final String message;

    NotificationErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
