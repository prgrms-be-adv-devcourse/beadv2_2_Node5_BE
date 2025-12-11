package com.node5.memberservice.member.application.dto;

import com.node5.memberservice.member.domain.MemberRole;
import com.node5.memberservice.member.presentation.dto.RoleAction;

public record RoleModifyCommand(
        MemberRole role,
        RoleAction action
) {
}
