package com.node5.shopservice.shop.application.dto;

import com.node5.shopservice.shop.domain.Shop;

import java.util.UUID;

public record ShopInfo(
        UUID id,
        String shopName
) {
    public static ShopInfo from(Shop shop) {
        return new ShopInfo(shop.getId(), shop.getShopName());
    }
}
