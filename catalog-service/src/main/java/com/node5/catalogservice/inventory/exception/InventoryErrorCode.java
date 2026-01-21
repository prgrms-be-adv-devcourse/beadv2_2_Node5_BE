package com.node5.catalogservice.inventory.exception;

import com.node5.common.exception.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InventoryErrorCode implements BaseErrorCode {

	INVALID_REQUEST(400, "INVENTORY_INVALID_REQUEST", "요청 값이 올바르지 않습니다."),
	INVALID_QUANTITY(400, "INVALID_QUANTITY", "수량은 0 이상이어야 합니다."),
	INVENTORY_NOT_FOUND(404, "INVENTORY_NOT_FOUND", "해당 상품의 재고가 존재하지 않습니다."),
	OUT_OF_STOCK(409, "OUT_OF_STOCK", "재고가 부족합니다."),
	RESERVATION_ALREADY_PROCESSED(409, "RESERVATION_ALREADY_PROCESSED", "이미 처리된 예약입니다."),

	RESERVATION_NOT_FOUND(404, "RESERVATION_NOT_FOUND", "해당 예약이 존재하지 않습니다."),
	RESERVATION_ALREADY_COMMITTED(409, "RESERVATION_ALREADY_COMMITTED", "이미 확정된 예약입니다."),
	RESERVATION_ALREADY_RELEASED(409, "RESERVATION_ALREADY_RELEASED", "이미 해제된 예약입니다."),

	PRODUCT_NOT_FOUND(404, "PRODUCT_NOT_FOUND", "상품이 존재하지 않습니다."),
	SELLER_FORBIDDEN(403, "SELLER_FORBIDDEN", "해당 상품에 대한 권한이 없습니다.");

	private final int status;
	private final String code;
	private final String message;
}
