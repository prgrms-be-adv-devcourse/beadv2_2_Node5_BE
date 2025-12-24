package com.node5.memberservice.member.application.dto;

import com.node5.memberservice.member.domain.Role;

import java.util.List;

public record RoleResponse(
        List<String> roles
) {
    public static RoleResponse from(List<Role> roles) {
        return new RoleResponse(roles.stream().map(role -> role.getName().name()).toList());
    }
}
