package com.node5.memberservice.member.application.dto;

import java.util.List;

public record RoleModifyResponse(
        String accessToken,
        List<String> memberRoles
) {
}
