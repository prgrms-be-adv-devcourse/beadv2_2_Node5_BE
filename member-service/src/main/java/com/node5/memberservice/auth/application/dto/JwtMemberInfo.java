package com.node5.memberservice.auth.application.dto;

import com.node5.memberservice.member.domain.Member;
import com.node5.memberservice.member.domain.MemberRole;

import java.util.Set;
import java.util.stream.Collectors;

public record JwtMemberInfo(
        String memberId,
        Set<String> memberRoles,
        String memberStatus
) {
    public static JwtMemberInfo from(Member member) {
        Set<String> roles = member.getRoles().stream().map(MemberRole::name).collect(Collectors.toSet());
        return new JwtMemberInfo(member.getId().toString(), roles, member.getStatus().name());
    }
}
