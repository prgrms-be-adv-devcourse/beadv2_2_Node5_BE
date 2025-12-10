package com.node5.shopservice.shop.application.dto;

import java.util.UUID;

public record ShopRegisterResponse(
        UUID shopId,
        String accessToken
) {

}
