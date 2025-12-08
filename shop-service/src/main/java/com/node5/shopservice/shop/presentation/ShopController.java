package com.node5.shopservice.shop.presentation;

import com.node5.common.domain.ApiResponseDto;
import com.node5.common.domain.PagedResponseDto;
import com.node5.shopservice.shop.application.ShopService;
import com.node5.shopservice.shop.application.dto.ShopInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/shops")
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<PagedResponseDto<ShopInfo>>> findAll(Pageable pageable) {
        return shopService.findAll(pageable);
    }

}
