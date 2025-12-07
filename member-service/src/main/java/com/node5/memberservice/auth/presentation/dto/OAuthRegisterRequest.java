package com.node5.memberservice.auth.presentation.dto;

import com.node5.memberservice.auth.application.dto.OAuthRegisterCommand;

public record OAuthRegisterRequest(
        String temporaryToken,
        String email,
        String name,
        String phoneNumber,
        String address
) {
    public OAuthRegisterCommand toCommand() {
        return new OAuthRegisterCommand(temporaryToken, email, name, phoneNumber, address);
    }
}
