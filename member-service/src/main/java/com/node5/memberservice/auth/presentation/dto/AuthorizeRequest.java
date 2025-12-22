package com.node5.memberservice.auth.presentation.dto;

import com.node5.memberservice.auth.application.dto.AuthorizeCommand;

import java.util.UUID;

public record AuthorizeRequest(
        UUID memberId,
        String method,
        String path
) {
    public AuthorizeCommand toCommand() {
        return new AuthorizeCommand(memberId, method, path);
    }
}
