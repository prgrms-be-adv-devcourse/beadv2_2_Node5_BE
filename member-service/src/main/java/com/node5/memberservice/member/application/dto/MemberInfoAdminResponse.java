package com.node5.memberservice.member.application.dto;

import com.node5.memberservice.member.domain.Member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MemberInfoAdminResponse(
        UUID id,
        String email,
        String name,
        String nickname,
        String phoneNumber,
        String address,
        List<String> roles,
        String status,
        LocalDateTime createdAt
) {

    public static MemberInfoAdminResponse from(Member member) {
        return new MemberInfoAdminResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getNickname(),
                member.getPhoneNumber(),
                member.getAddress(),
                member.getRoles().stream().map(Enum::name).toList(),
                member.getStatus().name(),
                member.getCreatedAt()
        );
    }
}
