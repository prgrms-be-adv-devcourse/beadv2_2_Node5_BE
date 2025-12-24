package com.node5.memberservice.auth.application.dto;

import com.node5.memberservice.member.domain.MemberRole;

public record EndpointKey(MemberRole role, String httpMethod) {
}
