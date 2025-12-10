package com.node5.shopservice.shop.application.dto;

public record ShopRegisterCommand(
        String shopEmail,
        String shopName,
        String shopPhoneNumber,
        String shopRegistrationNumber,
        String shopAddress
) {
}
