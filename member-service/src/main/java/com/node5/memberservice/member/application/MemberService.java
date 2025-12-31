package com.node5.memberservice.member.application;

import com.node5.memberservice.auth.domain.OAuthRepository;
import com.node5.memberservice.member.application.dto.*;
import com.node5.memberservice.member.domain.Member;
import com.node5.memberservice.member.domain.MemberRepository;
import com.node5.memberservice.member.domain.MemberRole;
import com.node5.memberservice.member.domain.MemberStatus;
import com.node5.memberservice.member.exception.MemberErrorCode;
import com.node5.memberservice.member.exception.MemberException;
import com.node5.memberservice.redis.application.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final OAuthRepository oAuthRepository;
    private final RedisService redisService;

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
        redisService.deleteRefreshToken(member.getId());
        oAuthRepository.deleteByMember(member);
    }

    @Transactional
    public void addMemberRole(UUID memberId, RoleModifyCommand command) {
        Member member = getMemberOrThrow(memberId);
        member.addRole(command.role());
    }

    @Transactional
    public void deleteMemberRole(UUID memberId, String role) {
        Member member = getMemberOrThrow(memberId);
        MemberRole roleEnum = Arrays.stream(MemberRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_ROLE));
        member.deleteRole(roleEnum);
    }

    private Member getMemberOrThrow(UUID memberId) {
        return memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Page<MemberInfoAdminResponse> getMembers(UUID adminId, Pageable pageable) {
        return memberRepository.findAllByIdNot(adminId, pageable).map(MemberInfoAdminResponse::from);
    }

    @Transactional
    public void modifyMemberStatus(UUID adminId, UUID memberId, MemberStatusModifyCommand command) {
        if (adminId.equals(memberId)) {
            throw new MemberException(MemberErrorCode.CANNOT_MODIFY_SELF);
        }
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.DELETED) {
            throw new MemberException(MemberErrorCode.DELETED_MEMBER_CANNOT_BE_MODIFIED);
        }

        member.modifyStatus(command.status());
    }

    public MemberStatusResponse getMemberStatuses() {
        return MemberStatusResponse.from(
                Arrays.stream(MemberStatus.values())
                        .filter(s -> s != MemberStatus.DELETED)
                        .toArray(MemberStatus[]::new)
        );
    }
}
