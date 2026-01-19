package com.node5.memberservice.shop.exception;

import com.node5.common.exception.BaseException;

public class ShopException extends BaseException {
    public ShopException(ShopErrorCode errorCode) {
        super(errorCode);
    }
}
