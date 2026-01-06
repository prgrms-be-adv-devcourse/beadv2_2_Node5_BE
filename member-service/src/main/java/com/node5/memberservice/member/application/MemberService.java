package com.node5.memberservice.member.application;

import com.node5.memberservice.auth.domain.OAuthRepository;
import com.node5.common.event.MemberDeletedEvent;
import com.node5.memberservice.member.application.dto.*;
import com.node5.memberservice.member.client.BillingClient;
import com.node5.memberservice.member.client.ShopClient;
import com.node5.memberservice.member.client.dto.WalletInfo;
import com.node5.memberservice.member.domain.*;
import com.node5.memberservice.member.exception.MemberErrorCode;
import com.node5.memberservice.member.exception.MemberException;
import com.node5.memberservice.redis.application.RedisService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final OAuthRepository oAuthRepository;
    private final RoleRepository roleRepository;
    private final RedisService redisService;
    private final ApplicationEventPublisher eventPublisher;
    private final ShopClient shopClient;
    private final BillingClient billingClient;

    public MemberInfoResponse findById(UUID memberId) {
        Member member = getNotDeletedMemberOrThrow(memberId);
        return MemberInfoResponse.from(member);
    }

    @Transactional
    public MemberInfoResponse modifyMember(UUID memberId, MemberModifyCommand command) {
        Member member = getNotDeletedMemberOrThrow(memberId);
        member.modifyInfo(command);
        return MemberInfoResponse.from(member);
    }

    // Todo - 트랜잭션 안에서 외부 서비스 호출이 있어 트랜잭션이 길어질 수 있다.
    @Transactional
    public void deleteMember(UUID memberId) {
        Member member = getNotDeletedMemberOrThrow(memberId);

        validateCanDeleteMember(member.getId());
        List<UUID> shopIds = getShopIds(member.getId());

        MemberDeletedEvent event = new MemberDeletedEvent(member.getId(), shopIds);

        member.delete();
        redisService.deleteRefreshToken(member.getId());
        oAuthRepository.deleteByMember(member);

        eventPublisher.publishEvent(event);
    }

    private void validateCanDeleteMember(UUID memberId) {
        try {
            // 예치금 잔액 확인
            WalletInfo wallet = billingClient.getWallet(memberId).getBody();
            if (wallet != null && wallet.balance() != 0) {
                throw new MemberException(MemberErrorCode.MEMBER_HAS_BALANCE);
            }
        } catch (FeignException.NotFound e) {
            // 지갑이 없는 회원 → 잔액 없음 → 탈퇴 가능
        } catch (FeignException e) {
            // billing 서비스가 응답했지만 오류
            throw new MemberException(MemberErrorCode.BILLING_SERVICE_UNAVAILABLE);
        }
        // Todo - 진행중인 주문 확인
        // Todo - 남은 정산 확인
    }

    private List<UUID> getShopIds(UUID memberId) {
        return shopClient.getShopIds(memberId).getBody();
    }

    @Transactional
    public void addMemberRole(UUID memberId, RoleModifyCommand command) {
        Member member = getNotDeletedMemberOrThrow(memberId);
        member.addRole(command.role());
    }

    @Transactional
    public void deleteMemberRole(UUID memberId, String role) {
        Member member = getNotDeletedMemberOrThrow(memberId);
        MemberRole roleEnum = Arrays.stream(MemberRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_ROLE));
        member.deleteRole(roleEnum);
    }

    private Member getNotDeletedMemberOrThrow(UUID memberId) {
        return memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Page<MemberInfoAdminResponse> getMembers(UUID adminId, Pageable pageable) {
        return memberRepository.findAllByIdNot(adminId, pageable).map(MemberInfoAdminResponse::from);
    }

    public RoleResponse getMemberRoles() {
        List<Role> roles = roleRepository.findAll();
        return RoleResponse.from(roles);
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
