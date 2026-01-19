package com.node5.memberservice.shop.presentation;

import com.node5.memberservice.shop.application.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("internal/shops")
public class ShopInternalController {

    private final ShopService shopService;

    @GetMapping("/{shopId}/member-id")
    public ResponseEntity<UUID> getMemberIdByShopId(@PathVariable UUID shopId){
        return ResponseEntity.ok(shopService.getMemberIdByShopId(shopId));
    }

}
