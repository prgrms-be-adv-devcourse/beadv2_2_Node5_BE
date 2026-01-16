package com.node5.memberservice.member.application;

import com.node5.common.event.MemberDeletedEvent;
import com.node5.memberservice.auth.domain.OAuthRepository;
import com.node5.memberservice.client.BillingClient;
import com.node5.memberservice.client.dto.WalletInfo;
import com.node5.memberservice.member.application.dto.*;
import com.node5.memberservice.member.domain.*;
import com.node5.memberservice.member.exception.MemberErrorCode;
import com.node5.memberservice.member.exception.MemberException;
import com.node5.memberservice.redis.application.RedisService;
import com.node5.memberservice.shop.domain.Shop;
import com.node5.memberservice.shop.domain.ShopRepository;
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
    private final ShopRepository shopRepository;
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

    @Transactional
    public void deleteMember(UUID memberId) {
        Member member = getNotDeletedMemberOrThrow(memberId);

        validateCanDeleteMember(member.getId());
        List<UUID> shopIds = getShopIds(memberId);

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
        List<Shop> shops = shopRepository.findAllByMemberIdAndDeletedAtIsNull(memberId);
        return shops.stream().map(Shop::getId).toList();
    }

    @Transactional
    public void addMemberRole(UUID memberId, MemberRole role) {
        Member member = getNotDeletedMemberOrThrow(memberId);
        member.addRole(role);
    }

    @Transactional
    public void deleteMemberRole(UUID memberId, MemberRole role) {
        Member member = getNotDeletedMemberOrThrow(memberId);
        member.deleteRole(role);
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

    public String getMemberEmail(String memberId) {
        UUID uuid;
        try {
            uuid = UUID.fromString(memberId);
        } catch (IllegalArgumentException e) {
            throw new MemberException(MemberErrorCode.INVALID_MEMBER_ID);
        }
        Member member = getNotDeletedMemberOrThrow(uuid);
        return member.getEmail();
    }

    public String getMemberNickname(UUID memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        return member.getNickname();
    }
}
