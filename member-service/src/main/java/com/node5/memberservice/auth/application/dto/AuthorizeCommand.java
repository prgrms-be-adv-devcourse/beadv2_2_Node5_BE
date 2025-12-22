package com.node5.memberservice.auth.application.dto;

import java.util.UUID;

public record AuthorizeCommand(
        UUID memberId,
        String method,
        String path
) {
}
