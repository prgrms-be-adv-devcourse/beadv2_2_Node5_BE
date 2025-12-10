package com.node5.memberservice.auth.oauth.dto;


public record OAuthUserInfo(
        String provider,
        String providerId
) {
}
