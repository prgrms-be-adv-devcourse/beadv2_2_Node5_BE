package com.node5.catalogservice.shop.dto;

import java.util.UUID;

public record ShopInfoResponse(
	UUID id,
	String shopName,
	String shopEmail,
	String shopPhoneNumber,
	String shopAddress
) {
}
