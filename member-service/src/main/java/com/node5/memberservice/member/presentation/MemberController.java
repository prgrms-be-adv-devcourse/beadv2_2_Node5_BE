package com.node5.memberservice.member.presentation;

import com.node5.memberservice.member.application.MemberService;
import com.node5.memberservice.member.application.dto.MemberInfo;
import com.node5.memberservice.member.presentation.dto.MemberModifyRequest;
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
    @GetMapping("/me")
    public ResponseEntity<MemberInfo> findById(@RequestHeader("Member-Id") UUID memberId) {
        return ResponseEntity.ok(memberService.findById(memberId));
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다.")
    @PutMapping("/me")
    public ResponseEntity<MemberInfo> modifyMember(@RequestHeader("Member-Id") UUID memberId, @RequestBody MemberModifyRequest request) {
        return ResponseEntity.ok(memberService.modifyMember(memberId, request.toCommand()));
    }

    @Operation(summary = "회원 삭제", description = "회원을 삭제합니다.")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMember(@RequestHeader("Member-Id") UUID memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.ok().build();
    }

}
