package com.node5.memberservice.member.presentation;

import com.node5.memberservice.member.application.MemberService;
import com.node5.memberservice.member.presentation.dto.RoleModifyRequest;
import jakarta.validation.Valid;
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
    public ResponseEntity<Void> addMemberRole(
            @PathVariable UUID memberId,
            @Valid @RequestBody RoleModifyRequest request
    ) {
        memberService.addMemberRole(memberId, request.toCommand());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{memberId}/roles/{role}")
    public ResponseEntity<Void> deleteMemberRole(
            @PathVariable UUID memberId,
            @PathVariable String role
    ) {
        memberService.deleteMemberRole(memberId, role);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{memberId}/email")
    public ResponseEntity<String> getMemberEmail(@PathVariable String memberId){
        return ResponseEntity.ok(memberService.getMemberEmail(memberId));
    }

    @GetMapping("/nickname")
    public String getMemberNickname(@RequestHeader("Member-Id") UUID memberId){
        return memberService.getMemberNickname(memberId);
    }

}
