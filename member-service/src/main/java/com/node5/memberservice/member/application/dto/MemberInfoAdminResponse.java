package com.node5.memberservice.member.application.dto;

import com.node5.memberservice.member.domain.Member;

import java.util.List;
import java.util.UUID;

public record MemberInfoAdminResponse(
        UUID id,
        String email,
        String name,
        String phoneNumber,
        String address,
        List<String> roles,
        String status
) {

    public static MemberInfoAdminResponse from(Member member) {
        return new MemberInfoAdminResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhoneNumber(),
                member.getAddress(),
                member.getRoles().stream().map(Enum::name).toList(),
                member.getStatus().name()
        );
    }
}
