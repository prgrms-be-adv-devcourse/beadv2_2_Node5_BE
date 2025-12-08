package com.node5.shopservice.shop.presentation;

import com.node5.common.domain.ApiResponseDto;
import com.node5.common.domain.PagedResponseDto;
import com.node5.shopservice.shop.application.ShopService;
import com.node5.shopservice.shop.application.dto.ShopDeleteResponse;
import com.node5.shopservice.shop.application.dto.ShopInfoResponse;
import com.node5.shopservice.shop.application.dto.ShopListResponse;
import com.node5.shopservice.shop.application.dto.ShopRegisterResponse;
import com.node5.shopservice.shop.presentation.dto.ShopRegisterRequest;
import com.node5.shopservice.shop.presentation.dto.ShopModifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/shops")
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<PagedResponseDto<ShopListResponse>>> findMyShopList(@RequestHeader("Member-Id") UUID memberId, Pageable pageable) {
        System.out.println("memberId: " + memberId);
        return shopService.findMyShopList(memberId, pageable);
    }

    @GetMapping("/{shopId}")
    public ResponseEntity<ApiResponseDto<ShopInfoResponse>> findMyShopInfo(@RequestHeader("Member-Id") UUID memberId, @PathVariable UUID shopId) {
        return shopService.findMyShopInfo(memberId, shopId);
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<ShopRegisterResponse>> registerShop(@RequestHeader("Member-Id") UUID memberId, @RequestBody ShopRegisterRequest request) {
        return shopService.registerShop(memberId, request.toCommand());
    }

    @PutMapping("/{shopId}")
    public ResponseEntity<ApiResponseDto<ShopInfoResponse>> modifyMyShopInfo(@RequestHeader("Member-Id") UUID memberId, @PathVariable UUID shopId, @RequestBody ShopModifyRequest request) {
        return shopService.modifyMyShopInfo(memberId, shopId, request.toCommand());
    }

    @DeleteMapping("/{shopId}")
    public ResponseEntity<ApiResponseDto<ShopDeleteResponse>> deleteMyShop(@RequestHeader("Member-Id") UUID memberId, @PathVariable UUID shopId) {
        return shopService.deleteMyShop(memberId, shopId);
    }

}
