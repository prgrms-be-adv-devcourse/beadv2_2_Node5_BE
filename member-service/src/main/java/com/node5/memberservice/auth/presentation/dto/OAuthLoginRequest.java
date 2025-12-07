package com.node5.memberservice.auth.presentation.dto;

import com.node5.memberservice.auth.application.dto.OAuthLoginCommand;

public record OAuthLoginRequest(
        String provider,
        String providerCode
) {

    public OAuthLoginCommand toCommand() {
        return new OAuthLoginCommand(provider, providerCode);
    }
}
