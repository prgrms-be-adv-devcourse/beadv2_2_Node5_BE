package com.node5.shopservice.shop.presentation;

import com.node5.shopservice.shop.application.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("internal/shops")
public class ShopInternalController {

    private final ShopService shopService;

    @GetMapping
    public ResponseEntity<String> getMemberIdByShopId(@RequestHeader("Shop-Id") UUID shopId){
        return ResponseEntity.ok(shopService.getMemberIdByShopId(shopId));
    }

}
