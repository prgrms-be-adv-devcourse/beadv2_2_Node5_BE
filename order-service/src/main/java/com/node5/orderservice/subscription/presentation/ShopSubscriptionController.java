package com.node5.orderservice.subscription.presentation;

import com.node5.orderservice.subscription.application.SubscriptionService;
import com.node5.orderservice.subscription.application.dto.SubscriptionInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "ShopSubscription", description = "상점 구독 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/shops/subscriptions")
public class ShopSubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "상품별 구독 리스트 조회", description = "자신이 판매 중인 상품에 걸린 구독 리스트를 조회합니다.")
    @GetMapping("{productId}")
    public ResponseEntity<Page<SubscriptionInfo>> findAllByProductId(@PathVariable UUID productId, Pageable pageable){
        return ResponseEntity.ok(subscriptionService.findAllByProductId(productId, pageable));
    }
}
