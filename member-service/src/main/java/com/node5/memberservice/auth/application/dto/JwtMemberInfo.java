package com.node5.memberservice.auth.application.dto;

import com.node5.memberservice.member.domain.Member;

public record JwtMemberInfo(
        String memberId,
        String memberStatus
) {
    public static JwtMemberInfo from(Member member) {
        return new JwtMemberInfo(member.getId().toString(), member.getStatus().name());
    }
}
