package com.node5.memberservice.member.presentation.dto;

import com.node5.memberservice.member.application.dto.MemberStatusModifyCommand;
import com.node5.memberservice.member.domain.MemberStatus;
import com.node5.memberservice.member.exception.MemberErrorCode;
import com.node5.memberservice.member.exception.MemberException;
import jakarta.validation.constraints.NotBlank;

import java.util.Arrays;

public record MemberStatusModifyRequest(
        @NotBlank(message = "status는 필수입니다.")
        String status
) {

    public MemberStatusModifyCommand toCommand() {
        MemberStatus statusEnum = Arrays.stream(MemberStatus.values())
                .filter(r -> r.name().equalsIgnoreCase(this.status))
                .findFirst()
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_STATUS));

        return new MemberStatusModifyCommand(statusEnum);
    }

}
