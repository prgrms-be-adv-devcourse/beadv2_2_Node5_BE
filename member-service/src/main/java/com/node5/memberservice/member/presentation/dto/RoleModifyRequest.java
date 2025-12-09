package com.node5.memberservice.member.presentation.dto;

import com.node5.memberservice.member.application.dto.RoleModifyCommand;

import java.util.Arrays;

public record RoleModifyRequest(
        String role,
        String action
) {
    public RoleModifyCommand toCommand() {
        RoleAction action = Arrays.stream(RoleAction.values())
                .filter(a -> a.name().equalsIgnoreCase(this.action))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid data: " + this.action));

        return new RoleModifyCommand(role, action);
    }
}
