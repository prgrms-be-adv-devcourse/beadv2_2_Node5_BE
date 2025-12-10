package com.node5.memberservice.auth.application.dto;

public record VerifyEmailCommand(
        String email,
        String verificationCode
) {
}
