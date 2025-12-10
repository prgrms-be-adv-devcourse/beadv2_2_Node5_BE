package com.node5.shopservice.shop.presentation.dto;

import com.node5.shopservice.shop.application.dto.ShopRegisterCommand;

public record ShopRegisterRequest(
        String shopEmail,
        String shopName,
        String shopPhoneNumber,
        String shopRegistrationNumber,
        String shopAddress
) {
    public ShopRegisterCommand toCommand() {
        return new ShopRegisterCommand(shopEmail, shopName, shopPhoneNumber, shopRegistrationNumber, shopAddress);
    }
}
