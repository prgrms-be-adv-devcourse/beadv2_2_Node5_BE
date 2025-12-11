package com.node5.shopservice.shop.presentation.dto;

import com.node5.shopservice.shop.application.dto.ShopRegisterCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ShopRegisterRequest(
        @NotBlank(message = "shopEmail은 필수입니다.")
        @Email(message = "shopEmail은 이메일 형식이어야 합니다.")
        String shopEmail,
        @NotBlank(message = "shopName은 필수입니다.")
        String shopName,
        @NotBlank(message = "shopPhoneNumber은 필수입니다.")
        String shopPhoneNumber,
        @NotBlank(message = "shopRegistrationNumber은 필수입니다.")
        String shopRegistrationNumber,
        @NotBlank(message = "shopAddress는 필수입니다.")
        String shopAddress
) {
    public ShopRegisterCommand toCommand() {
        return new ShopRegisterCommand(shopEmail, shopName, shopPhoneNumber, shopRegistrationNumber, shopAddress);
    }
}
