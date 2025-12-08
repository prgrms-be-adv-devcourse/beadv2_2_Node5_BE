package com.node5.shopservice.shop.presentation.dto;

import com.node5.shopservice.shop.application.dto.ShopModifyCommand;

public record ShopModifyRequest(
        String shopEmail,
        String shopName,
        String shopPhoneNumber,
        String shopAddress
) {
    public ShopModifyCommand toCommand() {
        return new ShopModifyCommand(shopEmail, shopName, shopPhoneNumber, shopAddress);
    }
}
