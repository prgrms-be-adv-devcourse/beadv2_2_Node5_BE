package com.node5.memberservice.member.presentation;

import com.node5.memberservice.member.application.MemberService;
import com.node5.memberservice.member.application.RoleService;
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
@RequestMapping("${api.v1}/admin")
public class AdminMemberController {

    private final RoleService roleService;
    private final MemberService memberService;

    @GetMapping("/roles")
    public ResponseEntity<RoleResponse> getRoles() {
        return ResponseEntity.ok(roleService.getRoles());
    }

    @GetMapping("/members")
    public ResponseEntity<Page<MemberInfoAdminResponse>> getMembers(Pageable pageable) {
        return ResponseEntity.ok(memberService.getMembers(pageable));
    }

    @GetMapping("/members/statuses")
    public ResponseEntity<MemberStatusResponse> getMemberStatuses() {
        return ResponseEntity.ok(memberService.getMemberStatuses());
    }


    @PatchMapping("/members/{memberId}/status")
    public ResponseEntity<Void> modifyMemberStatus(@PathVariable UUID memberId, @Valid @RequestBody MemberStatusModifyRequest request) {
        memberService.modifyMemberStatus(memberId, request.toCommand());
        return ResponseEntity.ok().build();
    }
}
