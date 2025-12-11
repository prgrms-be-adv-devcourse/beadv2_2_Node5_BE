package com.node5.memberservice.auth.application.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
