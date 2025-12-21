package com.node5.shopservice.shop.presentation;

import com.node5.shopservice.shop.application.ShopService;
import com.node5.shopservice.shop.application.dto.ShopInfoResponse;
import com.node5.shopservice.shop.application.dto.ShopListResponse;
import com.node5.shopservice.shop.presentation.dto.ShopModifyRequest;
import com.node5.shopservice.shop.presentation.dto.ShopRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "상점", description = "상점 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/shops")
public class ShopController {

    private final ShopService shopService;

    @Operation(summary = "내 상점 목록 조회", description = "인증된 회원의 상점 목록을 페이지로 가져옵니다.")
    @GetMapping
    public ResponseEntity<Page<ShopListResponse>> findMyShopList(
            @RequestHeader("Member-Id") UUID memberId,
            @PageableDefault(size = 10, page = 0, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(shopService.findMyShopList(memberId, pageable));
    }

    @Operation(summary = "내 상점 상세 조회", description = "인증된 회원이 소유한 상점의 상세 정보를 조회합니다.")
    @GetMapping("/{shopId}")
    public ResponseEntity<ShopInfoResponse> findMyShopInfo(
            @RequestHeader("Member-Id") UUID memberId,
            @PathVariable UUID shopId
    ) {
        return ResponseEntity.ok(shopService.findMyShopInfo(memberId, shopId));
    }

    @Operation(summary = "상점 등록", description = "인증된 회원을 위해 새 상점을 등록합니다.")
    @PostMapping
    public ResponseEntity<Void> registerShop(
            @RequestHeader("Member-Id") UUID memberId,
            @Valid @RequestBody ShopRegisterRequest request
    ) {
        shopService.registerShop(memberId, request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "내 상점 정보 수정", description = "인증된 회원이 소유한 상점의 상세 정보를 수정합니다.")
    @PutMapping("/{shopId}")
    public ResponseEntity<ShopInfoResponse> modifyMyShopInfo(
            @RequestHeader("Member-Id") UUID memberId,
            @PathVariable UUID shopId,
            @Valid @RequestBody ShopModifyRequest request
    ) {
        return ResponseEntity.ok(shopService.modifyMyShopInfo(memberId, shopId, request.toCommand()));
    }

    @Operation(summary = "내 상점 삭제", description = "인증된 회원이 소유한 상점을 삭제합니다.")
    @DeleteMapping("/{shopId}")
    public ResponseEntity<Void> deleteMyShop(
            @RequestHeader("Member-Id") UUID memberId,
            @PathVariable UUID shopId
    ) {
        shopService.deleteMyShop(memberId, shopId);
        return ResponseEntity.ok().build();
    }

}
