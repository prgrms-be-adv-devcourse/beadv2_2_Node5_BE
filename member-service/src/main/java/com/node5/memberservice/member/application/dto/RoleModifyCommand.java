package com.node5.memberservice.member.application.dto;

import com.node5.memberservice.member.domain.MemberRole;

public record RoleModifyCommand(
        MemberRole role
) {
}
