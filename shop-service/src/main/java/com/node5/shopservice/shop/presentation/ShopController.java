package com.node5.shopservice.shop.presentation;

import com.node5.common.domain.ApiResponseDto;
import com.node5.common.domain.PagedResponseDto;
import com.node5.shopservice.shop.application.ShopService;
import com.node5.shopservice.shop.application.dto.ShopInfo;
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

    // Todo: memberId는 param으로 안 들어오고 apigateway 통해서 헤더에 담겨 온다.
    @GetMapping
    public ResponseEntity<ApiResponseDto<PagedResponseDto<ShopInfo>>> findMyShopList(@RequestParam UUID memberId, Pageable pageable) {
        return shopService.findMyShops(memberId, pageable);
    }

    @GetMapping("/{shopId}")
    public ResponseEntity<ApiResponseDto<ShopInfo>> findMyShopInfo(@RequestParam UUID memberId, @PathVariable UUID shopId) {
        return shopService.findMyShopInfo(memberId, shopId);
    }


}
