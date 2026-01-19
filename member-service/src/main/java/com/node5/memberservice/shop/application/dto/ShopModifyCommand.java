package com.node5.memberservice.shop.application.dto;

public record ShopModifyCommand(
        String shopEmail,
        String shopName,
        String shopPhoneNumber,
        String shopAddress
) {
}
