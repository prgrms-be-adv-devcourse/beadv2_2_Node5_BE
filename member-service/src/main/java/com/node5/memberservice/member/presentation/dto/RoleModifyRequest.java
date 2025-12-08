package com.node5.memberservice.member.presentation.dto;

import com.node5.memberservice.member.application.dto.RoleModifyCommand;

public record RoleModifyRequest(
        String role,
        RoleAction action
) {
    public RoleModifyCommand toCommand() {
        return new RoleModifyCommand(role, action);
    }
}
