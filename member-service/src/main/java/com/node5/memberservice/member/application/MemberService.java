package com.node5.memberservice.member.application;

import com.node5.memberservice.auth.application.dto.JwtMemberInfo;
import com.node5.memberservice.auth.domain.OAuthRepository;
import com.node5.memberservice.auth.util.JwtProvider;
import com.node5.memberservice.member.application.dto.MemberInfo;
import com.node5.memberservice.member.application.dto.MemberModifyCommand;
import com.node5.memberservice.member.application.dto.RoleModifyCommand;
import com.node5.memberservice.member.domain.Member;
import com.node5.memberservice.member.domain.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final OAuthRepository oAuthRepository;
    private final JwtProvider jwtProvider;

    public MemberInfo findById(UUID memberId) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found: " + memberId));
        return MemberInfo.from(member);
    }

    @Transactional
    public MemberInfo modifyMember(UUID memberId, MemberModifyCommand command) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found: " + memberId));
        member.modifyInfo(command);
        return MemberInfo.from(member);
    }

    @Transactional
    public void deleteMember(UUID memberId) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found: " + memberId));
        member.delete();
        oAuthRepository.deleteByMember(member);

    }

    @Transactional
    public String modifyMemberRoles(UUID memberId, RoleModifyCommand command) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found: " + memberId));
        member.modifyRoles(command.role(), command.action());
        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);
        return jwtProvider.generateAccessToken(jwtMemberInfo);
    }
}
