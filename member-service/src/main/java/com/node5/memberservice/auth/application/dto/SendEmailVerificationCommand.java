package com.node5.memberservice.auth.application.dto;

public record SendEmailVerificationCommand(
        String temporaryToken,
        String email
) {

}
