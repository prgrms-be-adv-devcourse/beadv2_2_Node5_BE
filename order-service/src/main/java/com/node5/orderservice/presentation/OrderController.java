package com.node5.orderservice.presentation;

import com.node5.common.domain.ApiResponseDto;
import com.node5.orderservice.application.OrderService;
import com.node5.orderservice.application.dto.OrderCreateInfo;
import com.node5.orderservice.presentation.dto.OrderCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.v1}/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "주문 생성", description = "장바구니 상품이나 정기 구독 상품 주문 정보를 Order, OrderItem 테이블에 등록한다.")
    @PostMapping
    public ResponseEntity<ApiResponseDto<OrderCreateInfo>> create(@RequestBody @Valid OrderCreateRequest request){
        return orderService.create(request.toCommand());
    }

}
