package com.node5.memberservice.auth.presentation.dto;

import com.node5.memberservice.auth.application.dto.VerifyEmailCommand;

public record VerifyEmailRequest(
        String email,
        String verificationCode
) {
    public VerifyEmailCommand toCommand() {
        return new VerifyEmailCommand(email, verificationCode);
    }
}
