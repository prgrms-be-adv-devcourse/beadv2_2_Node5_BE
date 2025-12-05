package com.node5.memberservice.member.presentation.dto;

public record MemberRegisterRequest(
        String name,
        String phoneNumber,
        String address
) {
}
