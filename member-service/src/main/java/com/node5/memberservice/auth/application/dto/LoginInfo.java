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

    public static LoginInfo emailRequired(String temporaryToken) {
        return new LoginInfo(
                null,
                null,
                null,
                "EMAIL_REQUIRED",
                null,
                null,
                temporaryToken
        );
    }
}
