package com.node5.orderservice.order.application;

import com.node5.common.domain.PageInfoDto;
import com.node5.orderservice.order.application.dto.OrderCommand;
import com.node5.orderservice.order.application.dto.OrderCreateInfo;
import com.node5.orderservice.order.application.dto.OrderItemCommand;
import com.node5.orderservice.order.application.dto.OrderStatusInfo;
import com.node5.orderservice.order.client.dto.WalletInfo;
import com.node5.orderservice.order.client.dto.WalletRefundRequest;
import com.node5.orderservice.order.domain.Order;
import com.node5.orderservice.order.domain.OrderItem;
import com.node5.orderservice.order.domain.OrderItemRepository;
import com.node5.orderservice.order.domain.OrderRepository;
import com.node5.orderservice.order.exception.OrderAccessDeniedException;
import com.node5.orderservice.order.exception.OrderNotFoundException;
import com.node5.orderservice.order.exception.OrderPaymentFailedException;
import com.node5.orderservice.order.exception.OrderRequestNotAllowedException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import com.node5.orderservice.order.application.dto.*;
import com.node5.orderservice.order.client.BillingClient;
import com.node5.orderservice.order.client.dto.WalletWithdrawRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.node5.orderservice.order.domain.OrderStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderTransactionService orderTransactionService;
    private final BillingClient billingClient;

    @Transactional
    public OrderCreateInfo create(UUID memberId, OrderCommand command) {
        // 주문번호 생성, 총 주문 금액 계산하여 Order 생성
        String orderNum = generateNewOrderNum();
        Optional<BigDecimal> totalAmountOptional = command.items().stream()
                .map(OrderItemCommand::totalPrice)
                .reduce(BigDecimal::add);
        BigDecimal totalAmount = totalAmountOptional.orElse(BigDecimal.ZERO);

        Order order = Order.create(memberId, command, orderNum, totalAmount);
        Order saved = orderRepository.save(order);

        // OrderItem 생성
        UUID orderId = saved.getId();
        List<OrderItemCommand> itemCommands = command.items();
        List<OrderItem> orderItems = itemCommands.stream()
                .map(oi -> OrderItem.create(orderId, oi))
                .toList();
        orderItemRepository.saveAll(orderItems);

        // 예치금 사용 API 호출
        try {
            BigDecimal roundedAmount = order.getTotalAmount().setScale(0, RoundingMode.HALF_UP);
            ResponseEntity<WalletInfo> response = billingClient.withdraw(new WalletWithdrawRequest(order.getId(), roundedAmount.longValue()));

            if (response.getStatusCode().is2xxSuccessful()) {
                orderTransactionService.updateOrderStatus(orderId, PAID);
            }
        } catch(FeignException e) {
            orderTransactionService.updateOrderStatus(orderId, PAYMENT_FAILED);
            throw new OrderPaymentFailedException(orderId, e.getMessage());
        } catch(Exception e) {
            orderTransactionService.updateOrderStatus(orderId, PAYMENT_FAILED);
        }

        return OrderCreateInfo.from(saved);
    }

    public OrderListInfo getOrderList(UUID memberId, int page, int size, String period) {
        // 오늘 기준 n개월(period) 전 시점 구하기
        LocalDateTime nMonthsAgo = LocalDateTime.now().minusMonths(Integer.parseInt(period));

        // 페이징 설정
        Pageable pageable = PageRequest.of(page, size);

        // nMonthsAgo 이후 최근순 조회
        Page<Order> orderPage = orderRepository.findByMemberIdAndCreatedAtAfterOrderByCreatedAtDesc(memberId, nMonthsAgo, pageable);
        PageInfoDto pageInfo = new PageInfoDto(orderPage.getNumber(), orderPage.getSize(), orderPage.getTotalElements(), orderPage.getTotalPages());

        // 페이징 결과 처리
        if(!orderPage.hasContent()){
            return OrderListInfo.from(pageInfo, Collections.emptyList());
        }

        // - 주문 ID 목록 추출
        List<UUID> orderIds =  orderPage.getContent().stream()
                .map(Order::getId)
                .toList();

        // - 추출된 주문 ID 목록으로 모든 주문 상품 내역 조회
        List<OrderItem> orderedItems = orderItemRepository.findByOrderIdIn(orderIds);

        // - 데이터를 주문 ID별로 그룹핑
        Map<UUID, List<OrderItem>> orderGroup = orderedItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        // - OrderListDetailInfo DTO 생성
        List<OrderListInfo.OrderListDetailInfo> orderListInfos = orderPage.getContent().stream()
                .map(order -> {
                    List<OrderItem> orderItemList = orderGroup.get(order.getId());
                    List<OrderItemInfo> orderedItemInfos = orderItemList.stream()
                            .map(OrderItemInfo::from)
                            .toList();
            return OrderListInfo.OrderListDetailInfo.from(order, orderedItemInfos);
        }).toList();

        // - OrderListInfo DTO 생성 (OrderListDetailInfo + 페이징 정보)
        return OrderListInfo.from(pageInfo, orderListInfos);
    }

    public OrderDetailInfo getOrderDetail(UUID orderId, UUID memberId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if(!order.getMemberId().equals(memberId)){
            throw new OrderAccessDeniedException(order.getId(), memberId, "상세 조회");
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        List<OrderItemInfo> orderItemInfos = orderItems.stream()
                .map(OrderItemInfo::from)
                .toList();

        return OrderDetailInfo.from(order, orderItemInfos);
    }

    @Transactional
    public OrderStatusInfo cancel(UUID orderId, UUID memberId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if(!order.getMemberId().equals(memberId)){
            throw new OrderAccessDeniedException(order.getId(), memberId, "취소");
        }

        // 취소가 가능한 주문 상태인지 확인
        if(order.getStatus() == PAID){
            // 예치금 환불 API 호출
            try {
                BigDecimal roundedAmount = order.getTotalAmount().setScale(0, RoundingMode.HALF_UP);
                ResponseEntity<WalletInfo> response = billingClient.requestRefund(new WalletRefundRequest(order.getId(), roundedAmount.longValue()));

                if (response.getStatusCode().is2xxSuccessful()) {
                    orderTransactionService.updateOrderStatus(orderId, CANCELED);
                }
            } catch(FeignException e) {
                throw new OrderPaymentFailedException(orderId, e.getMessage());
            } catch(Exception e) {
                throw new OrderPaymentFailedException(orderId, e.getMessage());
            }
        }else{
            throw new OrderRequestNotAllowedException(
                    order.getId(),
                    order.getStatus(),
                    "취소는 주문의 상태가 결제 완료일 때 가능합니다."
            );
        }

        return OrderStatusInfo.from(order);
    }

    @Transactional
    public OrderStatusInfo refund(UUID orderId, UUID memberId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if(!order.getMemberId().equals(memberId)){
            throw new OrderAccessDeniedException(order.getId(), memberId, "환불");
        }

        // 환불이 가능한 주문 상태인지 확인
        if(order.getStatus() == DELIVERY_ING || order.getStatus() == DELIVERY_COMPLETED){
            // 예치금 환불 API 호출
            try {
                BigDecimal roundedAmount = order.getTotalAmount().setScale(0, RoundingMode.HALF_UP);
                ResponseEntity<WalletInfo> response = billingClient.requestRefund(new WalletRefundRequest(order.getId(), roundedAmount.longValue()));

                if (response.getStatusCode().is2xxSuccessful()) {
                    orderTransactionService.updateOrderStatus(orderId, REFUND_COMPLETED);
                }
            } catch(FeignException e) {
                throw new OrderPaymentFailedException(orderId, e.getMessage());
            } catch(Exception e) {
                throw new OrderPaymentFailedException(orderId, e.getMessage());
            }
        }else{
            throw new OrderRequestNotAllowedException(
                    order.getId(),
                    order.getStatus(),
                    "환불은 주문의 상태가 배송 중이거나 배송 완료일 때 가능합니다."
            );
        }

        return OrderStatusInfo.from(order);
    }

    private String generateNewOrderNum() {
        Long nextSequenceValue = orderRepository.getNextSequenceNum();

        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String sequencePart = String.format("%08d", nextSequenceValue);

        return datePart + "-" + sequencePart;
    }

}
