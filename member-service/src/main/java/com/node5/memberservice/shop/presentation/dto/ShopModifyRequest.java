package com.node5.memberservice.shop.presentation.dto;

import com.node5.memberservice.shop.application.dto.ShopModifyCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ShopModifyRequest(
        @Size(max = 100, message = "email은 100자를 초과할 수 없습니다.")
        @NotBlank(message = "shopEmail은 필수입니다.")
        @Email(message = "shopEmail은 이메일 형식이어야 합니다.")
        String shopEmail,
        @Size(max = 50, message = "shopName은 50자를 초과할 수 없습니다.")
        @NotBlank(message = "shopName은 필수입니다.")
        String shopName,
        @Pattern(
                regexp = "^01[0-9]{8,9}$",
                message = "phoneNumber 형식이 올바르지 않습니다."
        )
        @NotBlank(message = "shopPhoneNumber은 필수입니다.")
        String shopPhoneNumber,
        @Size(max = 100, message = "shopAddress는 100자를 초과할 수 없습니다.")
        @NotBlank(message = "shopAddress는 필수입니다.")
        String shopAddress
) {
    public ShopModifyCommand toCommand() {
        return new ShopModifyCommand(shopEmail, shopName, shopPhoneNumber, shopAddress);
    }
}
