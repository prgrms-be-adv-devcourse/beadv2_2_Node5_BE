package com.node5.memberservice.endpoint.presentation.dto;

import com.node5.memberservice.endpoint.application.dto.EndPointCommand;
import com.node5.memberservice.auth.exception.AuthErrorCode;
import com.node5.memberservice.auth.exception.AuthException;
import com.node5.memberservice.member.domain.MemberRole;
import jakarta.validation.constraints.NotBlank;

import java.util.Arrays;

public record EndPointRequest(
        @NotBlank(message = "role은 필수입니다.")
        String role,
        @NotBlank(message = "httpMethod는 필수입니다.")
        String httpMethod,
        @NotBlank(message = "pathPattern는 필수입니다.")
        String pathPattern
) {
    public EndPointCommand toCommand() {
        MemberRole memberRole = Arrays.stream(MemberRole.values())
                .filter(r -> r.name().equalsIgnoreCase(this.role))
                .findFirst()
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ROLE));

        return new EndPointCommand(memberRole, this.httpMethod, this.pathPattern);
    }
}
