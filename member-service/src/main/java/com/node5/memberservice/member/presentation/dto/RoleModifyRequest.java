package com.node5.memberservice.member.presentation.dto;

import com.node5.memberservice.member.application.dto.RoleModifyCommand;
import com.node5.memberservice.member.domain.MemberRole;
import com.node5.memberservice.member.exception.MemberErrorCode;
import com.node5.memberservice.member.exception.MemberException;
import jakarta.validation.constraints.NotBlank;

import java.util.Arrays;

public record RoleModifyRequest(
        @NotBlank(message = "role은 필수입니다.")
        String role,
        @NotBlank(message = "action은 필수입니다.")
        String action
) {
    public RoleModifyCommand toCommand() {
        RoleAction actionEnum = Arrays.stream(RoleAction.values())
                .filter(a -> a.name().equalsIgnoreCase(this.action))
                .findFirst()
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_ACTION));

        MemberRole roleEnum = Arrays.stream(MemberRole.values())
                .filter(r -> r.name().equalsIgnoreCase(this.role))
                .findFirst()
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_ROLE));

        return new RoleModifyCommand(roleEnum, actionEnum);
    }
}
