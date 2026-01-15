package com.node5.memberservice.member.application.dto;

import com.node5.memberservice.member.domain.MemberStatus;

public record MemberStatusModifyCommand(
        MemberStatus status
) {
}
