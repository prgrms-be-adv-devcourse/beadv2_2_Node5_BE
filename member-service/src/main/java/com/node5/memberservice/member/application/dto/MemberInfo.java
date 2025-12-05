package com.node5.memberservice.member.application.dto;

import com.node5.memberservice.member.domain.Member;

import java.util.UUID;

public record MemberInfo(
        UUID id,
        String email,
        String name,
        String phoneNumber,
        String address
) {

    public static MemberInfo from(Member member) {
        return new MemberInfo(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhoneNumber(),
                member.getAddress()
        );
    }
}
