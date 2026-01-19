package com.node5.memberservice.member.presentation.dto;

import com.node5.memberservice.member.application.dto.MemberModifyCommand;
import jakarta.validation.constraints.NotBlank;

public record MemberModifyRequest(
        @NotBlank(message = "name은 필수입니다.")
        String name,
        @NotBlank(message = "nickname은 필수입니다.")
        String nickname,
        @NotBlank(message = "phoneNumber는 필수입니다.")
        String phoneNumber,
        @NotBlank(message = "address는 필수입니다.")
        String address
) {
    public MemberModifyCommand toCommand() {
        return new MemberModifyCommand(name, nickname, phoneNumber, address);
    }
}
