package com.node5.memberservice.shop.application.dto;


import com.node5.memberservice.shop.domain.Shop;

import java.util.UUID;

public record ShopListResponse(
        UUID shopId,
        String shopName
) {
    public static ShopListResponse from(Shop shop) {
        return new ShopListResponse(shop.getId(), shop.getShopName());
    }
}
