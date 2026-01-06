package com.node5.shopservice.shop.application;

import com.node5.shopservice.shop.application.dto.ShopInfoResponse;
import com.node5.shopservice.shop.application.dto.ShopListResponse;
import com.node5.shopservice.shop.application.dto.ShopModifyCommand;
import com.node5.shopservice.shop.application.dto.ShopRegisterCommand;
import com.node5.common.event.ShopDeletedEvent;
import com.node5.shopservice.shop.client.BillingClient;
import com.node5.shopservice.shop.client.MemberClient;
import com.node5.shopservice.shop.client.dto.RoleAction;
import com.node5.shopservice.shop.client.dto.RoleModifyRequest;
import com.node5.shopservice.shop.domain.Shop;
import com.node5.shopservice.shop.domain.ShopRepository;
import com.node5.shopservice.shop.exception.ShopErrorCode;
import com.node5.shopservice.shop.exception.ShopException;
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

    private static final String ROLE_SELLER = "SELLER";

    private final ShopRepository shopRepository;
    private final MemberClient memberClient;
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

        // Todo - 보상 트랜잭션 필요
        updateMemberRoles(memberId, RoleAction.ADD);
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
            // Todo - 보상 트랜잭션 필요
            updateMemberRoles(memberId, RoleAction.REMOVE);
        }

        // 가게 삭제 topic 발행
        eventPublisher.publishEvent(shopDeletedEvent);
    }

    private void updateMemberRoles(UUID memberId, RoleAction action) {
        try {
            switch (action) {
                case ADD -> {
                    RoleModifyRequest request = new RoleModifyRequest(ROLE_SELLER);
                    memberClient.addMemberRole(memberId, request);
                }
                case REMOVE -> {
                    memberClient.deleteMemberRole(memberId, ROLE_SELLER);
                }
            }
            ;
        } catch (Exception e) {
            log.error("memberClient.updateMemberRoles error : {}", e.getMessage());
            throw new ShopException(ShopErrorCode.ROLE_UPDATE_FAILED);
        }
    }

    public UUID getMemberIdByShopId(UUID shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND)
        );

        return shop.getMemberId();
    }

    public List<UUID> getShopIds(UUID memberId) {
        List<Shop> shops = shopRepository.findAllByMemberIdAndDeletedAtIsNull(memberId);

        return shops.stream().map(Shop::getId).toList();
    }

    // Todo - 지금 delete 변경은 각 row 마다 변경 상점이 많아진다면 bulk update를 고려
    @Transactional
    public void deleteAllMyShop(UUID memberId) {
        List<Shop> shops = shopRepository.findAllByMemberIdAndDeletedAtIsNull(memberId);
        shops.forEach(Shop::delete);
    }
}
