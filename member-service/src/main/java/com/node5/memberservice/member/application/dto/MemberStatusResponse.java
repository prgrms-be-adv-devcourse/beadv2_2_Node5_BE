package com.node5.memberservice.member.application.dto;

import com.node5.memberservice.member.domain.MemberStatus;

import java.util.Arrays;
import java.util.List;

public record MemberStatusResponse(
        List<String> statuses
) {
    public static MemberStatusResponse from(MemberStatus[] statuses) {
        return new MemberStatusResponse(Arrays.stream(statuses).map(Enum::name).toList());
    }
}
