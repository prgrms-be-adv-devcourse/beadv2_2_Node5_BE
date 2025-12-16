package com.node5.memberservice.auth.application.dto;

import com.node5.memberservice.member.domain.Member;

import java.util.List;
import java.util.UUID;

public record LoginInfoResponse(
        MemberInfo memberInfo,
        String accessToken,
        String refreshToken,
        String temporaryToken
) {

    public static LoginInfoResponse success(Member member, String accessToken, String refreshToken) {
        return new LoginInfoResponse(
                new MemberInfo(member),
                accessToken,
                refreshToken,
                null
        );
    }

    public static LoginInfoResponse newMember(String temporaryToken) {
        return new LoginInfoResponse(
                null,
                null,
                null,
                temporaryToken
        );
    }

    public record MemberInfo(
            UUID id,
            String name,
            String status,
            List<String> roles
    ) {
        public MemberInfo(Member member) {
            this(member.getId(), member.getName(), member.getStatus().name(), member.getRoles().stream().map(Enum::name).toList());
        }
    }
}
