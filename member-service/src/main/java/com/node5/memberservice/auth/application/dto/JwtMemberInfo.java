package com.node5.memberservice.auth.application.dto;

import com.node5.memberservice.member.domain.MemberRole;
import com.node5.memberservice.member.domain.MemberStatus;

import java.util.UUID;

public record JwtMemberInfo(
        String memberId,
        String memberRole,
        String memberStatus
) {
    public static JwtMemberInfo from(
            UUID memberId,
            MemberRole memberRole,
            MemberStatus memberStatus
    ) {
        return new JwtMemberInfo(memberId.toString(), memberRole.name(), memberStatus.name());
    }
}
