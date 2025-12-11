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
    public ResponseEntity<String> addMemberRole(
            @PathVariable UUID memberId,
            @Valid @RequestBody RoleModifyRequest request
    ) {
        return ResponseEntity.ok(memberService.addMemberRole(memberId, request.toCommand()));
    }

    @DeleteMapping("/{memberId}/roles/{role}")
    public ResponseEntity<String> deleteMemberRole(
            @PathVariable UUID memberId,
            @PathVariable String role
    ) {
        return ResponseEntity.ok(memberService.deleteMemberRole(memberId, role));
    }

}
