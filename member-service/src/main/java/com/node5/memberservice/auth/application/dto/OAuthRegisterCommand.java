package com.node5.memberservice.auth.application.dto;

public record OAuthRegisterCommand(
        String temporaryToken,
        String email,
        String name,
        String nickname,
        String phoneNumber,
        String address
) {
}
