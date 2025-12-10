package com.node5.memberservice.member.presentation;

import com.node5.common.domain.ApiResponseDto;
import com.node5.memberservice.member.application.MemberService;
import com.node5.memberservice.member.application.dto.MemberInfo;
import com.node5.memberservice.member.presentation.dto.MemberRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 단건 조회", description = "회원 ID로 회원을 조회합니다.")
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponseDto<MemberInfo>> findById(@PathVariable UUID memberId) {
        return memberService.findById(memberId);
    }

    @Operation(summary = "회원 정보 등록", description = "회원의 필수 정보를 등록합니다.")
    @PostMapping("/{memberId}")
    public ResponseEntity<ApiResponseDto<MemberInfo>> registerRequiredInfo(@PathVariable UUID memberId, @RequestBody MemberRegisterRequest request) {
        return memberService.registerRequiredInfo(memberId, request);
    }

    @Operation(summary = "회원 삭제", description = "회원을 삭제합니다.")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponseDto<?>> deleteMember(@PathVariable UUID memberId) {
        return memberService.deleteMember(memberId);
    }

}
