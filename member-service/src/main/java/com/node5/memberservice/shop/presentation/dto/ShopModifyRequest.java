package com.node5.memberservice.shop.presentation.dto;

import com.node5.memberservice.shop.application.dto.ShopModifyCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ShopModifyRequest(
        @NotBlank(message = "shopEmail은 필수입니다.")
        @Email(message = "shopEmail은 이메일 형식이어야 합니다.")
        String shopEmail,
        @NotBlank(message = "shopName은 필수입니다.")
        String shopName,
        @NotBlank(message = "shopPhoneNumber은 필수입니다.")
        String shopPhoneNumber,
        @NotBlank(message = "shopAddress는 필수입니다.")
        String shopAddress
) {
    public ShopModifyCommand toCommand() {
        return new ShopModifyCommand(shopEmail, shopName, shopPhoneNumber, shopAddress);
    }
}
