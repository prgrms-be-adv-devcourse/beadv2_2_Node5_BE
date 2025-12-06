package com.node5.memberservice.auth.presentation.dto;

public record OAuthLoginRequest(
        String provider,
        String providerCode
) {
}
