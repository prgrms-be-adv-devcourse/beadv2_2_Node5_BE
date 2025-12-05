package com.node5.memberservice.member.presentation;

import com.node5.common.domain.ApiResponseDto;
import com.node5.memberservice.member.application.MemberService;
import com.node5.memberservice.member.application.dto.MemberInfo;
import com.node5.memberservice.member.presentation.dto.MemberRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponseDto<MemberInfo>> findById(@PathVariable UUID memberId) {
        return memberService.findById(memberId);
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<MemberInfo>> create(String email) {
        return memberService.create(email);
    }

    @PostMapping("/{memberId}")
    public ResponseEntity<ApiResponseDto<MemberInfo>> registerRequiredInfo(@PathVariable UUID memberId, @RequestBody MemberRegisterRequest request) {
        return memberService.registerRequiredInfo(memberId, request);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponseDto<?>> deleteMember(@PathVariable UUID memberId) {
        return memberService.deleteMember(memberId);
    }

}
