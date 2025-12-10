package com.node5.memberservice.member.application.dto;

public record MemberModifyCommand(
        String name,
        String phoneNumber,
        String address
) {
}
