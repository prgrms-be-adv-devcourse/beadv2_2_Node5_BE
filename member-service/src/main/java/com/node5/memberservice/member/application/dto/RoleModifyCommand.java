package com.node5.memberservice.member.application.dto;

import com.node5.memberservice.member.presentation.dto.RoleAction;

public record RoleModifyCommand(
        String role,
        RoleAction action
) {
}
