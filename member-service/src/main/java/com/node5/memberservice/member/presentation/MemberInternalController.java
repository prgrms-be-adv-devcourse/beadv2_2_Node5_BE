package com.node5.memberservice.member.presentation;

import com.node5.memberservice.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/members")
public class MemberInternalController {

    private final MemberService memberService;

    @GetMapping("/{memberId}/email")
    public ResponseEntity<String> getMemberEmail(@PathVariable String memberId){
        return ResponseEntity.ok(memberService.getMemberEmail(memberId));
    }

    @GetMapping("/nickname")
    public String getMemberNickname(@RequestHeader("Member-Id") UUID memberId){
        return memberService.getMemberNickname(memberId);
    }

}
