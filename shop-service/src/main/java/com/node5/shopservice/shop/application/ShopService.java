package com.node5.shopservice.shop.application;

import com.node5.shopservice.shop.application.dto.*;
import com.node5.shopservice.shop.client.MemberClient;
import com.node5.shopservice.shop.client.dto.RoleAction;
import com.node5.shopservice.shop.client.dto.RoleModifyRequest;
import com.node5.shopservice.shop.domain.Shop;
import com.node5.shopservice.shop.domain.ShopRepository;
import com.node5.shopservice.shop.exception.ShopErrorCode;
import com.node5.shopservice.shop.exception.ShopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private static final String ROLE_SELLER = "SELLER";

    private final ShopRepository shopRepository;
    private final MemberClient memberClient;

    public Page<ShopListResponse> findMyShopList(UUID memberId, Pageable pageable) {
        return shopRepository.findAllByMemberIdAndDeletedAtIsNull(memberId, pageable).map(ShopListResponse::from);
    }

    public ShopInfoResponse findMyShopInfo(UUID memberId, UUID shopId) {
        Shop shop = getOwnedShopOrThrow(memberId, shopId);
        return ShopInfoResponse.from(shop);
    }

    @Transactional
    public ShopRegisterResponse registerShop(UUID memberId, ShopRegisterCommand command) {
        Shop shop = Shop.create(memberId, command);
        Shop savedShop = shopRepository.save(shop);

        String newAccessToken = updateMemberRoles(memberId, RoleAction.ADD);

        return new ShopRegisterResponse(savedShop.getId(), newAccessToken);
    }

    @Transactional
    public ShopInfoResponse modifyMyShopInfo(UUID memberId, UUID shopId, ShopModifyCommand command) {
        Shop shop = getOwnedShopOrThrow(memberId, shopId);
        shop.update(command);
        return ShopInfoResponse.from(shop);
    }

    @Transactional
    public ShopDeleteResponse deleteMyShop(UUID memberId, UUID shopId) {
        Shop shop = getOwnedShopOrThrow(memberId, shopId);
        shop.delete();
        shopRepository.flush();

        int shopCount = shopRepository.countByMemberIdAndDeletedAtIsNull(memberId);
        String newAccessToken = null;
        if (shopCount == 0) {
            newAccessToken = updateMemberRoles(memberId, RoleAction.REMOVE);
        }
        return new ShopDeleteResponse(newAccessToken);
    }

    private Shop getShopOrThrow(UUID shopId) {
        return shopRepository.findByIdAndDeletedAtIsNull(shopId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));
    }

    private Shop getOwnedShopOrThrow(UUID memberId, UUID shopId) {
        Shop shop = getShopOrThrow(shopId);
        if (!shop.getMemberId().equals(memberId)) {
            throw new ShopException(ShopErrorCode.SHOP_NOT_OWNED);
        }
        return shop;
    }

    private String updateMemberRoles(UUID memberId, RoleAction action) {
        try {
            return switch (action) {
                case ADD -> {
                    RoleModifyRequest request = new RoleModifyRequest(ROLE_SELLER);
                    ResponseEntity<String> response = memberClient.addMemberRole(memberId, request);
                    yield response.getBody();
                }
                case REMOVE -> {
                    ResponseEntity<String> response = memberClient.deleteMemberRole(memberId, ROLE_SELLER);
                    yield response.getBody();
                }
            };
        } catch (Exception e) {
            log.error("memberClient.updateMemberRoles error : {}", e.getMessage());
            throw new ShopException(ShopErrorCode.ROLE_UPDATE_FAILED);
        }
    }
}
