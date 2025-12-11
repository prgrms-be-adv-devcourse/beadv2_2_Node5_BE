package com.node5.memberservice.auth.presentation.dto;

import com.node5.memberservice.auth.application.dto.RefreshTokenCommand;

public record RefreshTokenRequest(
        String refreshToken
) {
    public RefreshTokenCommand toCommand() {
        return new RefreshTokenCommand(refreshToken);
    }
}
