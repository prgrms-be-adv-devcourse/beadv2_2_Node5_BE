package com.node5.memberservice.member.application.dto;

import com.node5.memberservice.member.domain.Member;

import java.util.UUID;

public record MemberInfoResponse(
        UUID id,
        String email,
        String name,
        String nickname,
        String phoneNumber,
        String address
) {

    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getNickname(),
                member.getPhoneNumber(),
                member.getAddress()
        );
    }
}
