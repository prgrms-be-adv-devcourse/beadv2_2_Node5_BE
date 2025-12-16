package com.node5.shopservice.shop.application;

import com.node5.shopservice.shop.application.dto.*;
import com.node5.shopservice.shop.client.MemberClient;
import com.node5.shopservice.shop.client.dto.RoleAction;
import com.node5.shopservice.shop.client.dto.RoleModifyRequest;
import com.node5.shopservice.shop.client.dto.RoleModifyResponse;
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
        Shop shop = shopRepository.findByIdAndMemberIdAndDeletedAtIsNull(shopId, memberId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));
        return ShopInfoResponse.from(shop);
    }

    @Transactional
    public ShopRegisterResponse registerShop(UUID memberId, ShopRegisterCommand command) {
        Shop shop = Shop.create(memberId, command);
        shopRepository.save(shop);

        RoleModifyResponse modifyMemberRoles = updateMemberRoles(memberId, RoleAction.ADD);

        return new ShopRegisterResponse(modifyMemberRoles.accessToken(), modifyMemberRoles.memberRoles());
    }

    @Transactional
    public ShopInfoResponse modifyMyShopInfo(UUID memberId, UUID shopId, ShopModifyCommand command) {
        Shop shop = shopRepository.findByIdAndMemberIdAndDeletedAtIsNull(shopId, memberId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));
        shop.update(command);
        return ShopInfoResponse.from(shop);
    }

    @Transactional
    public ShopDeleteResponse deleteMyShop(UUID memberId, UUID shopId) {
        Shop shop = shopRepository.findByIdAndMemberIdAndDeletedAtIsNull(shopId, memberId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));
        shop.delete();
        shopRepository.flush();

        int shopCount = shopRepository.countByMemberIdAndDeletedAtIsNull(memberId);
        if (shopCount == 0) {
            RoleModifyResponse modifyMemberRoles = updateMemberRoles(memberId, RoleAction.REMOVE);
            return new ShopDeleteResponse(modifyMemberRoles.accessToken(), modifyMemberRoles.memberRoles());
        }

        return new ShopDeleteResponse(null, null);
    }

    private RoleModifyResponse updateMemberRoles(UUID memberId, RoleAction action) {
        try {
            return switch (action) {
                case ADD -> {
                    RoleModifyRequest request = new RoleModifyRequest(ROLE_SELLER);
                    ResponseEntity<RoleModifyResponse> response = memberClient.addMemberRole(memberId, request);
                    yield response.getBody();
                }
                case REMOVE -> {
                    ResponseEntity<RoleModifyResponse> response = memberClient.deleteMemberRole(memberId, ROLE_SELLER);
                    yield response.getBody();
                }
            };
        } catch (Exception e) {
            log.error("memberClient.updateMemberRoles error : {}", e.getMessage());
            throw new ShopException(ShopErrorCode.ROLE_UPDATE_FAILED);
        }
    }

    public String getMemberIdByShopId(UUID shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND)
        );

        return shop.getMemberId().toString();
    }
}
