package com.node5.orderservice.global.openfeign.client;

import com.node5.orderservice.global.openfeign.client.dto.SettlementSourceItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "member-service")
public interface MemberClient {

    @GetMapping("internal/shops/{shopId}/member-id")
    ResponseEntity<UUID> getMemberIdByShopId(@PathVariable UUID shopId);

    @PostMapping("/internal/settlements/source")
    ResponseEntity<Void> settle(@RequestBody List<SettlementSourceItem> items);
}
