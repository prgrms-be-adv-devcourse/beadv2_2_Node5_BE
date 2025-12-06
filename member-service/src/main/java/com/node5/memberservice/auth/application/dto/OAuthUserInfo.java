package com.node5.memberservice.auth.application.dto;


public record OAuthUserInfo(
        String provider,
        String providerId
) {
}
