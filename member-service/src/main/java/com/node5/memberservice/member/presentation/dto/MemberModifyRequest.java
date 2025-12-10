package com.node5.memberservice.member.presentation.dto;

import com.node5.memberservice.member.application.dto.MemberModifyCommand;

public record MemberModifyRequest(
        String name,
        String phoneNumber,
        String address
) {
    public MemberModifyCommand toCommand() {
        return new MemberModifyCommand(name, phoneNumber, address);
    }
}
