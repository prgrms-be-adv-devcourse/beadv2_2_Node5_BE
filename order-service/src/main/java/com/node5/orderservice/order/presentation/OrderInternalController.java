package com.node5.orderservice.order.presentation;

import com.node5.orderservice.order.application.OrderInternalService;
import com.node5.orderservice.order.presentation.dto.OrderStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/orders")
public class OrderInternalController {

    @Autowired
    private OrderInternalService orderInternalService;

    @GetMapping("/getRecentOrderList")
    public ResponseEntity<List<UUID>> getRecentOrderList(
            @RequestHeader("Member-Id") UUID memberId
    ){
        return ResponseEntity.ok(orderInternalService.getRecentOrderList(memberId));
    }

    // 상품에 대한 리뷰 작성 가능 여부 확인
    @GetMapping("/review-status")
    public ResponseEntity<Boolean> getOrderStatus(
            @RequestHeader("Member-Id") UUID memberId,
            @RequestBody OrderStatusRequest request
    ) {
        return ResponseEntity.ok(orderInternalService.getOrderStatus(memberId, request.toCommand()));
    }

}
