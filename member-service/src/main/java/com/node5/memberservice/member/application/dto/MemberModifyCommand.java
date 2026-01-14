package com.node5.memberservice.member.application.dto;

public record MemberModifyCommand(
        String name,
        String nickname,
        String phoneNumber,
        String address
) {
}
