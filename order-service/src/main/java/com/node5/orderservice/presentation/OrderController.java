package com.node5.orderservice.presentation;

import com.node5.orderservice.application.OrderService;
import com.node5.orderservice.application.dto.*;
import com.node5.orderservice.presentation.dto.OrderCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Order", description = "주문 API")
@RestController
@RequestMapping("${api.v1}/orders")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "주문 생성", description = "장바구니 상품이나 정기 구독 상품 주문 정보를 Order, OrderItem 테이블에 등록한다.")
    @PostMapping
    public ResponseEntity<OrderCreateInfo> create(@RequestBody @Valid OrderCreateRequest request) {
        return orderService.create(request.toCommand());
    }

    @Operation(summary = "주문 목록 조회", description = "일정 기간동안의 모든 주문 내역을 페이징 조회한다.")
    @GetMapping
    public ResponseEntity<OrderListInfo> getOrderList(
            @RequestParam("memberId") UUID memberId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "period", defaultValue = "3")
            @Pattern(regexp = "^(3|6|12)$", message = "가능한 조회 기간은 3개월, 6개월, 12개월 입니다.") String period
    ) {
        return orderService.getOrderList(memberId, page, size, period);
    }

    @Operation(summary = "주문 상세 조회", description = "주문 ID로부터 특정 주문 상세 내역을 조회한다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailInfo> getOrder(@PathVariable("orderId") UUID id) {
        return orderService.getOrderDetail(id);
    }

    @Operation(summary = "주문 취소", description = "주문의 상태를 CANCEL_REQUESTED로 변경하고, 결제 취소가 완료되면 CANCEL_COMPLETED로 변경한다.")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderStatusInfo> cancel(
            @PathVariable("orderId") UUID orderId,
            @RequestParam("memberId") UUID memberId
    ) {
        return orderService.cancel(orderId, memberId);
    }

    @Operation(summary = "주문 환불", description = "주문의 상태를 REFUND_REQUESTED로 변경하고, 결제 취소가 완료되면 REFUND_COMPLETED로 변경한다.")
    @PatchMapping("/{orderId}/refund")
    public ResponseEntity<OrderStatusInfo> refund(
            @PathVariable("orderId") UUID orderId,
            @RequestParam("memberId") UUID memberId
    ) {
        return orderService.refund(orderId, memberId);
    }

}
