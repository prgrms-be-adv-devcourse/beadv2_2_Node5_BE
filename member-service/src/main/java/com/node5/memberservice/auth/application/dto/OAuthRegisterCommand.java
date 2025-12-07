package com.node5.memberservice.auth.application.dto;

public record OAuthRegisterCommand(
        String temporaryToken,
        String email,
        String name,
        String phoneNumber,
        String address
) {
}
