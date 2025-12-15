package com.node5.shopservice.shop.application.dto;

import java.util.List;

public record ShopRegisterResponse(
        String accessToken,
        List<String> memberRoles
) {

}
