package com.node5.shopservice.shop.client.dto;

import java.util.List;

public record RoleModifyResponse(
        String accessToken,
        List<String> memberRoles
) {
}
