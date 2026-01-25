package com.node5.memberservice.auth.presentation.dto;

import com.node5.memberservice.auth.application.dto.AuthorizeCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AuthorizeRequest(
        @NotNull(message = "memberId는 필수입니다.")
        UUID memberId,
        @NotBlank(message = "httpMethod는 필수입니다.")
        String httpMethod,
        @NotBlank(message = "path는 필수입니다.")
        String path
) {
    public AuthorizeCommand toCommand() {
        return new AuthorizeCommand(memberId, httpMethod, path);
    }
}
