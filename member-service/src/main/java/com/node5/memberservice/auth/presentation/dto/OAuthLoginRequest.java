package com.node5.memberservice.auth.presentation.dto;

import com.node5.memberservice.auth.application.dto.OAuthLoginCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OAuthLoginRequest(
        @Size(max = 20, message = "provider는 20자를 초과할 수 없습니다.")
        @NotBlank(message = "provider은 필수 입니다.")
        String provider,
        @Size(max = 100, message = "providerCode는 100자를 초과할 수 없습니다.")
        @NotBlank(message = "providerCode는 필수 입니다.")
        String providerCode,
        String redirectUrl
) {

    public OAuthLoginCommand toCommand() {
        return new OAuthLoginCommand(provider, providerCode, redirectUrl);
    }
}
