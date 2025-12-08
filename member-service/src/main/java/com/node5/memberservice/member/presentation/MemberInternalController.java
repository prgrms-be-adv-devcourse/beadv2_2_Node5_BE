package com.node5.memberservice.member.presentation;

import com.node5.common.domain.ApiResponseDto;
import com.node5.memberservice.member.application.MemberService;
import com.node5.memberservice.member.presentation.dto.RoleModifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/members")
public class MemberInternalController {

    private final MemberService memberService;

    @PostMapping("/{memberId}/roles")
    public ResponseEntity<ApiResponseDto<String>> modifyMemberRoles(@PathVariable UUID memberId, @RequestBody RoleModifyRequest request) {
        return memberService.modifyMemberRoles(memberId, request.toCommand());
    }

}
