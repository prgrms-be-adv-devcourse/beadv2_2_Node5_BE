package com.node5.supportservice.chat.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements BaseErrorCode {
    OPENAI_API_KEY_MISSING(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SUPPORT_CHAT_001", "OpenAI API Key가 없습니다."),
    OPENAI_REQUEST_FAILED(HttpStatus.SERVICE_UNAVAILABLE.value(), "SUPPORT_CHAT_002", "OpenAI 요청에 실패했습니다."),
    OPENAI_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY.value(), "SUPPORT_CHAT_003", "OpenAI 응답이 비어 있습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
