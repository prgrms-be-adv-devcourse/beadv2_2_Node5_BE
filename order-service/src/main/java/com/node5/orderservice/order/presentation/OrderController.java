package com.node5.orderservice.order.presentation;

import com.node5.orderservice.order.application.OrderService;
import com.node5.orderservice.order.application.dto.OrderCreateInfo;
import com.node5.orderservice.order.application.dto.OrderDetailInfo;
import com.node5.orderservice.order.application.dto.OrderListInfo;
import com.node5.orderservice.order.application.dto.OrderStatusInfo;
import com.node5.orderservice.order.presentation.dto.OrderCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<OrderCreateInfo> create(
            @RequestHeader("Member-Id") UUID memberId,
            @RequestBody @Valid OrderCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.create(memberId, request.toCommand()));
    }

    @Operation(summary = "주문 목록 조회", description = "일정 기간동안의 모든 주문 내역을 페이징 조회한다.")
    @GetMapping
    public ResponseEntity<OrderListInfo> getOrderList(
            @RequestHeader("Member-Id") UUID memberId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "period", defaultValue = "3")
            @Pattern(regexp = "^(3|6|12)$", message = "가능한 조회 기간은 3개월, 6개월, 12개월 입니다.") String period
    ) {
        return ResponseEntity.ok(orderService.getOrderList(memberId, page, size, period));
    }

    @Operation(summary = "주문 상세 조회", description = "주문 ID로부터 특정 주문 상세 내역을 조회한다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailInfo> getOrder(
            @PathVariable("orderId") UUID orderId,
            @RequestHeader("Member-Id") UUID memberId
    ) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderId, memberId));
    }

    @Operation(summary = "주문 취소", description = "주문의 상태가 PAID인 경우 취소가 가능하며, 결제 취소 성공 시 주문의 상태가 CANCELED로 변경된다.")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderStatusInfo> cancel(
            @PathVariable("orderId") UUID orderId,
            @RequestHeader("Member-Id") UUID memberId
    ) {
        return ResponseEntity.ok(orderService.cancel(orderId, memberId));
    }

//    @Operation(summary = "주문 환불", description = "주문의 상태가 DELIVERY_*인 경우 취소가 가능하며, 결제 취소 성공 시 주문의 상태가 REFUND_COMPLETED로 변경된다.")
//    @PatchMapping("/{orderId}/refund")
//    public ResponseEntity<OrderStatusInfo> refund(
//            @PathVariable("orderId") UUID orderId,
//            @RequestHeader("Member-Id") UUID memberId
//    ) {
//        return ResponseEntity.ok(orderService.refund(orderId, memberId));
//    }

}
