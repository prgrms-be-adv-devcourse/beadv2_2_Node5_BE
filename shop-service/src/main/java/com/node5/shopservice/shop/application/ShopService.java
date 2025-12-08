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

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    public ResponseEntity<ApiResponseDto<PagedResponseDto<ShopInfo>>> findAll(Pageable pageable) {
        Page<Shop> pagedShops = shopRepository.findAll(pageable);
        List<ShopInfo> shops = pagedShops.stream().map(ShopInfo::from).toList();
        PageInfoDto pageInfoDto = new PageInfoDto(pagedShops.getNumber(), pagedShops.getSize(), pagedShops.getTotalElements(), pagedShops.getTotalPages());
        PagedResponseDto<ShopInfo> pagedResponseDto = new PagedResponseDto<>(shops, pageInfoDto);
        ApiResponseDto<PagedResponseDto<ShopInfo>> response = new ApiResponseDto<>(HttpStatus.OK.value(), "OK", pagedResponseDto);
        return ResponseEntity.ok(response);
    }
}
