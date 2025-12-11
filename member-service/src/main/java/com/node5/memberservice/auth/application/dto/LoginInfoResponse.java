package com.node5.memberservice.auth.application.dto;

import com.node5.memberservice.member.domain.Member;

import java.util.UUID;

public record LoginInfoResponse(
        UUID id,
        String memberName,
        String memberStatus,
        String loginStatus,
        String accessToken,
        String refreshToken,
        String temporaryToken
) {

    public static LoginInfoResponse success(Member member, String accessToken, String refreshToken) {
        return new LoginInfoResponse(
                member.getId(),
                member.getName(),
                member.getStatus().name(),
                "SUCCESS",
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
                "NEW_MEMBER",
                null,
                null,
                temporaryToken
        );
    }
}
