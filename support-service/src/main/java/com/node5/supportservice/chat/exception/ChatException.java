package com.node5.supportservice.chat.exception;

import com.node5.common.exception.BaseErrorCode;
import com.node5.common.exception.BaseException;

public class ChatException extends BaseException {
    public ChatException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
