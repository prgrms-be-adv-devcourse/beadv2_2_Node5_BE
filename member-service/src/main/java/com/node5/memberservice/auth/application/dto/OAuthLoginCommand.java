package com.node5.memberservice.auth.application.dto;

public record OAuthLoginCommand(
        String provider,
        String providerCode
) {
}
