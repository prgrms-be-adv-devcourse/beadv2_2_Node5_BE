package com.node5.memberservice.shop.application.dto;

public record ShopRegisterCommand(
        String shopEmail,
        String shopName,
        String shopPhoneNumber,
        String shopRegistrationNumber,
        String shopAddress
) {
}
