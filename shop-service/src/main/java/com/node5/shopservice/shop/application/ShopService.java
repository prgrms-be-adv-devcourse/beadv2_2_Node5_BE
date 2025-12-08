package com.node5.shopservice.shop.application;

import com.node5.common.domain.ApiResponseDto;
import com.node5.common.domain.PageInfoDto;
import com.node5.common.domain.PagedResponseDto;
import com.node5.shopservice.shop.application.dto.ShopInfo;
import com.node5.shopservice.shop.domain.Shop;
import com.node5.shopservice.shop.domain.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    public ResponseEntity<ApiResponseDto<PagedResponseDto<ShopInfo>>> findMyShops(UUID memberId, Pageable pageable) {
        Page<Shop> pagedMyShops = shopRepository.findAllByMemberId(memberId, pageable);
        List<ShopInfo> myShops = pagedMyShops.stream().map(ShopInfo::from).toList();
        PageInfoDto pageInfoDto = new PageInfoDto(pagedMyShops.getNumber(), pagedMyShops.getSize(), pagedMyShops.getTotalElements(), pagedMyShops.getTotalPages());
        PagedResponseDto<ShopInfo> pagedResponseDto = new PagedResponseDto<>(myShops, pageInfoDto);
        ApiResponseDto<PagedResponseDto<ShopInfo>> response = new ApiResponseDto<>(HttpStatus.OK.value(), "OK", pagedResponseDto);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponseDto<ShopInfo>> findMyShopInfo(UUID memberId, UUID shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new IllegalArgumentException("Shop not found: " + shopId));
        if (!shop.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Shop is not yours");
        }
        ShopInfo shopInfo = ShopInfo.from(shop);
        ApiResponseDto<ShopInfo> response = new ApiResponseDto<>(HttpStatus.OK.value(), "OK", shopInfo);
        return ResponseEntity.ok(response);
    }
}
