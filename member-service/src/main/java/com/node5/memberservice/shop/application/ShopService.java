package com.node5.memberservice.shop.application;

import com.node5.common.event.ShopDeletedEvent;
import com.node5.memberservice.client.BillingClient;
import com.node5.memberservice.member.application.MemberService;
import com.node5.memberservice.member.domain.MemberRole;
import com.node5.memberservice.shop.application.dto.ShopInfoResponse;
import com.node5.memberservice.shop.application.dto.ShopListResponse;
import com.node5.memberservice.shop.application.dto.ShopModifyCommand;
import com.node5.memberservice.shop.application.dto.ShopRegisterCommand;
import com.node5.memberservice.shop.domain.Shop;
import com.node5.memberservice.shop.domain.ShopRepository;
import com.node5.memberservice.shop.exception.ShopErrorCode;
import com.node5.memberservice.shop.exception.ShopException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopRepository shopRepository;
    private final MemberService memberService;
    private final BillingClient billingClient;
    private final ApplicationEventPublisher eventPublisher;

    public Page<ShopListResponse> findMyShopList(UUID memberId, Pageable pageable) {
        return shopRepository.findAllByMemberIdAndDeletedAtIsNull(memberId, pageable).map(ShopListResponse::from);
    }

    public ShopInfoResponse findMyShopInfo(UUID memberId, UUID shopId) {
        Shop shop = shopRepository.findByIdAndMemberIdAndDeletedAtIsNull(shopId, memberId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));
        return ShopInfoResponse.from(shop);
    }

    @Transactional
    public void registerShop(UUID memberId, ShopRegisterCommand command) {
        checkWalletExists(memberId);

        Shop shop = Shop.create(memberId, command);
        shopRepository.save(shop);

        memberService.addMemberRole(memberId, MemberRole.SELLER);
    }

    private void checkWalletExists(UUID memberId) {
        try {
            billingClient.getWallet(memberId);
        } catch (FeignException.NotFound e) {
            throw new ShopException(ShopErrorCode.WALLET_REQUIRED);
        } catch (Exception e) {
            log.error("billingClient.getWallet error", e);
            throw new ShopException(ShopErrorCode.UNCAUGHT_EXCEPTION);
        }
    }

    @Transactional
    public ShopInfoResponse modifyMyShopInfo(UUID memberId, UUID shopId, ShopModifyCommand command) {
        Shop shop = shopRepository.findByIdAndMemberIdAndDeletedAtIsNull(shopId, memberId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));
        shop.update(command);
        return ShopInfoResponse.from(shop);
    }

    @Transactional
    public void deleteMyShop(UUID memberId, UUID shopId) {
        Shop shop = shopRepository.findByIdAndMemberIdAndDeletedAtIsNull(shopId, memberId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));

        ShopDeletedEvent shopDeletedEvent = new ShopDeletedEvent(shop.getId());

        shop.delete();
        shopRepository.flush();

        int shopCount = shopRepository.countByMemberIdAndDeletedAtIsNull(memberId);
        if (shopCount == 0) {
            memberService.deleteMemberRole(memberId, MemberRole.SELLER);
        }

        // 가게 삭제 topic 발행
        eventPublisher.publishEvent(shopDeletedEvent);
    }

    // Todo - 삭제된 shop 이면?
    public UUID getMemberIdByShopId(UUID shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND)
        );

        return shop.getMemberId();
    }

    // Todo - 지금 delete 변경은 각 row 마다 변경 상점이 많아진다면 bulk update를 고려
    @Transactional
    public void deleteAllMyShop(UUID memberId) {
        List<Shop> shops = shopRepository.findAllByMemberIdAndDeletedAtIsNull(memberId);
        shops.forEach(Shop::delete);
    }
}
