package com.node5.memberservice.member.presentation;

import com.node5.memberservice.member.application.MemberService;
import com.node5.memberservice.member.application.dto.MemberInfoAdminResponse;
import com.node5.memberservice.member.application.dto.MemberStatusResponse;
import com.node5.memberservice.member.application.dto.RoleResponse;
import com.node5.memberservice.member.presentation.dto.MemberStatusModifyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<Page<MemberInfoAdminResponse>> getMembers(@RequestHeader("Member-Id") UUID adminId, Pageable pageable) {
        return ResponseEntity.ok(memberService.getMembers(adminId, pageable));
    }

    @GetMapping("/roles")
    public ResponseEntity<RoleResponse> getMemberRoles() {
        return ResponseEntity.ok(memberService.getMemberRoles());
    }

    @GetMapping("/statuses")
    public ResponseEntity<MemberStatusResponse> getMemberStatuses() {
        return ResponseEntity.ok(memberService.getMemberStatuses());
    }


    @PatchMapping("/{memberId}/status")
    public ResponseEntity<Void> modifyMemberStatus(
            @RequestHeader("Member-Id") UUID adminId,
            @PathVariable UUID memberId,
            @Valid @RequestBody MemberStatusModifyRequest request
    ) {
        memberService.modifyMemberStatus(adminId, memberId, request.toCommand());
        return ResponseEntity.ok().build();
    }
}
