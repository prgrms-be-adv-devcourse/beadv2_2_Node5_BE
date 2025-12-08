package com.node5.shopservice.shop.application.dto;

import com.node5.shopservice.shop.domain.Shop;

import java.util.UUID;

public record ShopInfoResponse(
        UUID id,
        String shopName,
        String shopEmail,
        String shopPhoneNumber,
        String shopAddress
) {
    public static ShopInfoResponse from(Shop shop) {

        return new ShopInfoResponse(
                shop.getId(),
                shop.getShopName(),
                shop.getShopEmail(),
                shop.getShopPhoneNumber(),
                shop.getShopAddress()
        );
    }
}
