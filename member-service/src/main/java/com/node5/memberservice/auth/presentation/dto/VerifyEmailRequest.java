package com.node5.memberservice.auth.presentation.dto;

import com.node5.memberservice.auth.application.dto.VerifyEmailCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyEmailRequest(
        @NotBlank(message = "email은 필수 입니다.")
        @Email(message = "email 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "verificationCode는 필수 입니다.")
        @Pattern(regexp = "\\d{6}", message = "verificationCode는 6자리 숫자여야 합니다.")
        String verificationCode
) {
    public VerifyEmailCommand toCommand() {
        return new VerifyEmailCommand(email, verificationCode);
    }
}
