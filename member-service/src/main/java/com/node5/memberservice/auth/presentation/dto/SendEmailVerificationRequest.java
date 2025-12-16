package com.node5.memberservice.auth.presentation.dto;

import com.node5.memberservice.auth.application.dto.SendEmailVerificationCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendEmailVerificationRequest(
        @NotBlank(message = "temporaryToken은 필수 입니다.")
        String temporaryToken,
        @NotBlank(message = "email은 필수 입니다.")
        @Email(message = "email 형식이 올바르지 않습니다.")
        String email
) {
    public SendEmailVerificationCommand toCommand() {
        return new SendEmailVerificationCommand(temporaryToken, email);
    }
}
