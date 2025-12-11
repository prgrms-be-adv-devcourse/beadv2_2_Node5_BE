package com.node5.memberservice.member.application;

import com.node5.memberservice.auth.application.dto.JwtMemberInfo;
import com.node5.memberservice.auth.domain.OAuthRepository;
import com.node5.memberservice.auth.util.JwtProvider;
import com.node5.memberservice.member.application.dto.MemberInfoResponse;
import com.node5.memberservice.member.application.dto.MemberModifyCommand;
import com.node5.memberservice.member.application.dto.RoleModifyCommand;
import com.node5.memberservice.member.domain.Member;
import com.node5.memberservice.member.domain.MemberRepository;
import com.node5.memberservice.member.exception.MemberErrorCode;
import com.node5.memberservice.member.exception.MemberException;
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

    public MemberInfoResponse findById(UUID memberId) {
        Member member = getMemberOrThrow(memberId);
        return MemberInfoResponse.from(member);
    }

    @Transactional
    public MemberInfoResponse modifyMember(UUID memberId, MemberModifyCommand command) {
        Member member = getMemberOrThrow(memberId);
        member.modifyInfo(command);
        return MemberInfoResponse.from(member);
    }

    @Transactional
    public void deleteMember(UUID memberId) {
        Member member = getMemberOrThrow(memberId);
        member.delete();
        oAuthRepository.deleteByMember(member);
    }

    @Transactional
    public String modifyMemberRoles(UUID memberId, RoleModifyCommand command) {
        Member member = getMemberOrThrow(memberId);
        member.modifyRoles(command.role(), command.action());

        JwtMemberInfo jwtMemberInfo = JwtMemberInfo.from(member);
        return jwtProvider.generateAccessToken(jwtMemberInfo);
    }

    private Member getMemberOrThrow(UUID memberId) {
        return memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
