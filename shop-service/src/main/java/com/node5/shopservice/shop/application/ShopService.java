package com.node5.shopservice.shop.application;

import com.node5.common.domain.ApiResponseDto;
import com.node5.shopservice.shop.application.dto.*;
import com.node5.shopservice.shop.client.MemberClient;
import com.node5.shopservice.shop.client.dto.RoleAction;
import com.node5.shopservice.shop.client.dto.RoleModifyRequest;
import com.node5.shopservice.shop.domain.Shop;
import com.node5.shopservice.shop.domain.ShopRepository;
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
public class ShopService {

    private static final String ROLE_SELLER = "SELLER";

    private final ShopRepository shopRepository;
    private final MemberClient memberClient;

    @Transactional(readOnly = true)
    public Page<ShopListResponse> findMyShopList(UUID memberId, Pageable pageable) {
        Page<Shop> pagedMyShops = shopRepository.findAllByMemberIdAndDeletedAtIsNull(memberId, pageable);
        return pagedMyShops.map(ShopListResponse::from);
    }

    @Transactional(readOnly = true)
    public ShopInfoResponse findMyShopInfo(UUID memberId, UUID shopId) {
        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId).orElseThrow(() -> new IllegalArgumentException("Shop not found: " + shopId));
        if (!shop.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Shop is not yours");
        }
        return ShopInfoResponse.from(shop);
    }

    @Transactional
    public ShopRegisterResponse registerShop(UUID memberId, ShopRegisterCommand command) {
        Shop shop = Shop.create(memberId, command);
        Shop savedShop = shopRepository.save(shop);

        String accessToken = updateMemberRoles(memberId, RoleAction.ADD);

        return new ShopRegisterResponse(savedShop.getId(), accessToken);
    }

    @Transactional
    public ShopInfoResponse modifyMyShopInfo(UUID memberId, UUID shopId, ShopModifyCommand command) {
        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId).orElseThrow(() -> new IllegalArgumentException("Shop not found: " + shopId));
        if (!shop.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Shop is not yours");
        }
        shop.update(command);

        return ShopInfoResponse.from(shop);
    }

    @Transactional
    public ShopDeleteResponse deleteMyShop(UUID memberId, UUID shopId) {
        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId).orElseThrow(() -> new IllegalArgumentException("Shop not found: " + shopId));
        if (!shop.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Shop is not yours");
        }
        shop.delete();
        shopRepository.flush();

        int shopCount = shopRepository.countByMemberIdAndDeletedAtIsNull(memberId);
        String accessToken = null;
        if (shopCount == 0) {
            accessToken = updateMemberRoles(memberId, RoleAction.REMOVE);
        }
        return new ShopDeleteResponse(accessToken);
    }

    private String updateMemberRoles(UUID memberId, RoleAction action) {
        try {
            RoleModifyRequest request = new RoleModifyRequest(ROLE_SELLER, action);
            ResponseEntity<ApiResponseDto<String>> response = memberClient.modifyMemberRoles(memberId, request);
            return response.getBody().data();
        } catch (Exception e) {
            log.error("memberClient.updateMemberRoles error : {}", e.getMessage());
            throw new RuntimeException("권한 업데이트 실패: " + e);
        }
    }
}
