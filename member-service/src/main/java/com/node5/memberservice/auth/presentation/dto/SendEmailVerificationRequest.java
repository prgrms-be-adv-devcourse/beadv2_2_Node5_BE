package com.node5.memberservice.auth.presentation.dto;

import com.node5.memberservice.auth.application.dto.SendEmailVerificationCommand;

public record SendEmailVerificationRequest(
        String temporaryToken,
        String email
) {
    public SendEmailVerificationCommand toCommand() {
        return new SendEmailVerificationCommand(temporaryToken, email);
    }
}
