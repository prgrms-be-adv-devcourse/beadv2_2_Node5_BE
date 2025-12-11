package com.node5.shopservice.shop.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ShopErrorCode implements BaseErrorCode {
    SHOP_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "SHOP_001", "상점을 찾을 수 없습니다."),
    SHOP_NOT_OWNED(HttpStatus.FORBIDDEN.value(), "SHOP_002", "해당 상점에 대한 권한이 없습니다."),
    ROLE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SHOP_003", "회원 권한 업데이트에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    ShopErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
