package com.node5.memberservice.auth.application.dto;

import com.node5.memberservice.member.domain.Member;

import java.util.UUID;

public record LoginInfo(
        UUID id,
        String memberName,
        String memberStatus,
        String loginStatus,
        String accessToken,
        String refreshToken,
        String temporaryToken
) {

    public static LoginInfo success(Member member, String accessToken, String refreshToken) {
        return new LoginInfo(
                member.getId(),
                member.getName(),
                member.getStatus().name(),
                "SUCCESS",
                accessToken,
                refreshToken,
                null
        );
    }

    public static LoginInfo newMember(String temporaryToken) {
        return new LoginInfo(
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
