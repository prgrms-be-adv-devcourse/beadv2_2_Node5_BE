package com.node5.shopservice.shop.client.dto;

public record RoleModifyRequest(
        String role,
        RoleAction action
) {
}
