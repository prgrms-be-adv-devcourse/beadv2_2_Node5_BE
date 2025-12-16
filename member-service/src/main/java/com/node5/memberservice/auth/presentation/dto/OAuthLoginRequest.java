package com.node5.memberservice.auth.presentation.dto;

import com.node5.memberservice.auth.application.dto.OAuthLoginCommand;
import jakarta.validation.constraints.NotBlank;

public record OAuthLoginRequest(
        @NotBlank(message = "provider은 필수 입니다.")
        String provider,
        @NotBlank(message = "providerCode는 필수 입니다.")
        String providerCode
) {

    public OAuthLoginCommand toCommand() {
        return new OAuthLoginCommand(provider, providerCode);
    }
}
