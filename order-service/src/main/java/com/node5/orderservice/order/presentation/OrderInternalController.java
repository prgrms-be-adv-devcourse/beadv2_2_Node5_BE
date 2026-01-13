package com.node5.orderservice.order.presentation;

import com.node5.orderservice.order.application.OrderInternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
