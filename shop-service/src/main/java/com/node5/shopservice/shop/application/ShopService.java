package com.node5.shopservice.shop.application;

import com.node5.common.domain.ApiResponseDto;
import com.node5.common.domain.PageInfoDto;
import com.node5.common.domain.PagedResponseDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final MemberClient memberClient;

    public ResponseEntity<ApiResponseDto<PagedResponseDto<ShopListResponse>>> findMyShopList(UUID memberId, Pageable pageable) {
        Page<Shop> pagedMyShops = shopRepository.findAllByMemberIdAndDeletedAtIsNull(memberId, pageable);
        List<ShopListResponse> myShops = pagedMyShops.stream().map(ShopListResponse::from).toList();
        PageInfoDto pageInfoDto = new PageInfoDto(pagedMyShops.getNumber(), pagedMyShops.getSize(), pagedMyShops.getTotalElements(), pagedMyShops.getTotalPages());
        PagedResponseDto<ShopListResponse> pagedResponseDto = new PagedResponseDto<>(myShops, pageInfoDto);
        ApiResponseDto<PagedResponseDto<ShopListResponse>> response = new ApiResponseDto<>(HttpStatus.OK.value(), "OK", pagedResponseDto);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponseDto<ShopInfoResponse>> findMyShopInfo(UUID memberId, UUID shopId) {
        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId).orElseThrow(() -> new IllegalArgumentException("Shop not found: " + shopId));
        if (!shop.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Shop is not yours");
        }
        ShopInfoResponse shopInfoResponse = ShopInfoResponse.from(shop);
        ApiResponseDto<ShopInfoResponse> response = new ApiResponseDto<>(HttpStatus.OK.value(), "OK", shopInfoResponse);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<ShopRegisterResponse>> registerShop(UUID memberId, ShopRegisterCommand command) {
        Shop shop = Shop.create(memberId, command);
        Shop savedShop = shopRepository.save(shop);

        String accessToken = updateMemberRoles(memberId, "SELLER", RoleAction.ADD);

        ShopRegisterResponse shopRegisterResponse = new ShopRegisterResponse(savedShop.getId(), accessToken);
        ApiResponseDto<ShopRegisterResponse> response = new ApiResponseDto<>(HttpStatus.OK.value(), "OK", shopRegisterResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<ShopInfoResponse>> modifyMyShopInfo(UUID memberId, UUID shopId, ShopModifyCommand command) {
        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId).orElseThrow(() -> new IllegalArgumentException("Shop not found: " + shopId));
        if (!shop.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Shop is not yours");
        }
        shop.update(command);

        ShopInfoResponse shopInfoResponse = ShopInfoResponse.from(shop);
        ApiResponseDto<ShopInfoResponse> response = new ApiResponseDto<>(HttpStatus.OK.value(), "OK", shopInfoResponse);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<ShopDeleteResponse>> deleteMyShop(UUID memberId, UUID shopId) {
        Shop shop = shopRepository.findByIdAndDeletedAtIsNull(shopId).orElseThrow(() -> new IllegalArgumentException("Shop not found: " + shopId));
        if (!shop.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Shop is not yours");
        }
        shop.delete();
        shopRepository.flush();

        int shopCount = shopRepository.countByMemberIdAndDeletedAtIsNull(memberId);
        String accessToken = null;
        if (shopCount == 0) {
            accessToken = updateMemberRoles(memberId, "SELLER", RoleAction.REMOVE);
        }
        ShopDeleteResponse shopDeleteResponse = new ShopDeleteResponse(accessToken);
        ApiResponseDto<ShopDeleteResponse> response = new ApiResponseDto<>(HttpStatus.OK.value(), "OK", shopDeleteResponse);
        return ResponseEntity.ok(response);
    }

    private String updateMemberRoles(UUID memberId, String role, RoleAction action) {
        try {
            RoleModifyRequest request = new RoleModifyRequest(role, action);
            ResponseEntity<ApiResponseDto<String>> response = memberClient.modifyMemberRoles(memberId, request);
            return response.getBody().data();
        } catch (Exception e) {
            log.error("memberClient.updateMemberRoles error : {}", e.getMessage());
            throw new RuntimeException("권한 업데이트 실패: " + e);
        }
    }
}
