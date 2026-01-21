package com.node5.orderservice.order.application;

import com.node5.common.domain.PageInfoDto;
import com.node5.orderservice.global.openfeign.client.CatalogClient;
import com.node5.orderservice.global.openfeign.client.WalletClient;
import com.node5.orderservice.global.openfeign.client.dto.*;
import com.node5.orderservice.order.application.dto.OrderCommand;
import com.node5.orderservice.order.application.dto.OrderCreateInfo;
import com.node5.orderservice.order.application.dto.OrderItemCommand;
import com.node5.orderservice.order.application.dto.OrderStatusInfo;;
import com.node5.orderservice.order.domain.Order;
import com.node5.orderservice.order.domain.OrderItem;
import com.node5.orderservice.order.domain.OrderItemRepository;
import com.node5.orderservice.order.domain.OrderRepository;
import com.node5.orderservice.order.exception.*;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import com.node5.orderservice.order.application.dto.*;
import lombok.extern.slf4j.Slf4j;
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
import static com.node5.orderservice.order.exception.OrderErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderTransactionService orderTransactionService;
    private final WalletClient walletClient;
    private final CatalogClient catalogClient;
    private final FeignErrorDecoderUtil feignUtil;

    @Transactional
    public OrderCreateInfo create(UUID memberId, OrderCommand command) {
        if (command.subscriptionKey() != null) {
            Optional<Order> existing = orderRepository.findBySubscriptionKey(command.subscriptionKey());
            if (existing.isPresent()) {
                return OrderCreateInfo.from(existing.get());
            }
        }

        // 재고 선점 API 호출 (catalog-client 연동)
        UUID orderId = UUID.randomUUID();
        StockHoldBatchRequest holdRequest = new StockHoldBatchRequest(
                orderId,
                command.items().stream()
                        .map(i -> new StockHoldBatchRequest.StockHoldItemRequest(i.productId(), i.quantity()))
                        .toList()
        );

        try {
            catalogClient.hold(holdRequest);
        } catch(FeignException e) {
            throw new OrderException(ORDER_STOCK_HOLD_FAILED, "message=" + feignUtil.getFeignErrorMessage(e));
        }

        // Order 생성 (주문번호 생성, 총 주문 금액 계산)
        String orderNum = generateNewOrderNum();
        Optional<BigDecimal> totalAmountOptional = command.items().stream()
                .map(OrderItemCommand::totalPrice)
                .reduce(BigDecimal::add);
        BigDecimal totalAmount = totalAmountOptional.orElse(BigDecimal.ZERO);

        Order order = Order.create(orderId, memberId, command, orderNum, totalAmount);
        Order saved = orderRepository.save(order);

        // OrderItem 생성
        List<OrderItemCommand> itemCommands = command.items();
        List<OrderItem> orderItems = itemCommands.stream()
                .map(oi -> OrderItem.create(orderId, oi))
                .toList();
        orderItemRepository.saveAll(orderItems);

        // 예치금 사용 API 호출 (billing-client 연동)
        boolean paid = false;
        try {
            BigDecimal roundedAmount = order.getTotalAmount().setScale(0, RoundingMode.HALF_UP);
            walletClient.withdraw(memberId, new WalletWithdrawRequest(order.getId(), roundedAmount.longValue()));

            // 결제 성공 기록
            saved.markAsPaid(LocalDateTime.now());
            paid = true;
        } catch(FeignException e) {
            orderTransactionService.updateOrderStatus(orderId, PAYMENT_FAILED);
            throw new OrderException(ORDER_PAYMENT_FAILED, "orderId=" + orderId + ", message=" + feignUtil.getFeignErrorMessage(e));
        } catch(Exception e) {
            orderTransactionService.updateOrderStatus(orderId, PAYMENT_FAILED);
            throw new OrderException(ORDER_PAYMENT_FAILED, "orderId=" + orderId + ", message=" + e.getMessage());
        } finally {
            if (paid) {
                commitStock(orderId, command);
            } else {
                releaseStock(orderId, command);
            }
        }

        return OrderCreateInfo.from(saved);
    }

    private void commitStock(UUID orderId, OrderCommand command) {
        try {
            List<StockCommitBatchRequest.StockCommitItemRequest> items = uniqueProductIds(command).stream()
                    .map(StockCommitBatchRequest.StockCommitItemRequest::new)
                    .toList();

            catalogClient.commit(new StockCommitBatchRequest(orderId, items));
        } catch (FeignException e) {
            log.error("재고 확정 실패: orderId={}, message={}", orderId, feignUtil.getFeignErrorMessage(e));
        } catch (Exception e) {
            log.error("재고 확정 실패: orderId={}, message={}", orderId, e.getMessage(), e);
        }
    }

    private void releaseStock(UUID orderId, OrderCommand command) {
        try {
            List<StockReleaseBatchRequest.StockReleaseItemRequest> items = uniqueProductIds(command).stream()
                    .map(StockReleaseBatchRequest.StockReleaseItemRequest::new)
                    .toList();

            catalogClient.release(new StockReleaseBatchRequest(orderId, items));
        } catch (FeignException e) {
            log.error("재고 해제 실패: orderId={}, message={}", orderId, feignUtil.getFeignErrorMessage(e));
        } catch (Exception e) {
            log.error("재고 해제 실패: orderId={}, message={}", orderId, e.getMessage(), e);
        }
    }

    private List<UUID> uniqueProductIds(OrderCommand command) {
        return command.items().stream()
                .map(OrderItemCommand::productId)
                .distinct()
                .toList();
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
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND, "orderId=" + orderId));

        if(!order.getMemberId().equals(memberId)){
            String msg = "[상세 조회] memberId: " + memberId + ", orderId: " + orderId;
            throw new OrderException(ORDER_ACCESS_DENIED, msg);
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
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND, "orderId=" + orderId));

        if(!order.getMemberId().equals(memberId)){
            String msg = "[취소] memberId: " + memberId + ", orderId: " + order.getId();
            throw new OrderException(ORDER_ACCESS_DENIED, msg);
        }

        // 취소가 가능한 주문 상태인지 확인
        if(order.getStatus() == PAID){
            // 예치금 환불 API 호출
            try {
                BigDecimal roundedAmount = order.getTotalAmount().setScale(0, RoundingMode.HALF_UP);
                ResponseEntity<WalletInfo> response = walletClient.requestRefund(memberId, new WalletRefundRequest(order.getId(), roundedAmount.longValue()));

                if (response.getStatusCode().is2xxSuccessful()) {
                    orderTransactionService.updateOrderStatus(orderId, CANCELED);
                }
            } catch (Exception e) {
                throw new OrderException(ORDER_PAYMENT_FAILED, "orderId=" + orderId + ", message=" + e.getMessage());
            }
        }else{
            String msg = String.format("취소는 주문의 상태가 결제 완료일 때 가능합니다.(orderId: %s, orderStatus: %s)",
                    order.getId(), order.getStatus());
            throw new OrderException(ORDER_REQUEST_NOT_ALLOWED, msg);
        }

        return OrderStatusInfo.from(order);
    }

//    @Transactional
//    public OrderStatusInfo refund(UUID orderId, UUID memberId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND, "orderId=" + orderId));
//
//        if(!order.getMemberId().equals(memberId)){
//            String msg = "[환불] memberId: " + memberId + ", orderId: " + order.getId();
//            throw new OrderException(ORDER_ACCESS_DENIED, msg);
//        }
//
//        // 환불이 가능한 주문 상태인지 확인
//        if(order.getStatus() == DELIVERY_ING || order.getStatus() == DELIVERY_COMPLETED){
//            // 예치금 환불 API 호출
//            try {
//                BigDecimal roundedAmount = order.getTotalAmount().setScale(0, RoundingMode.HALF_UP);
//                ResponseEntity<WalletInfo> response = billingClient.requestRefund(memberId, new WalletRefundRequest(order.getId(), roundedAmount.longValue()));
//
//                if (response.getStatusCode().is2xxSuccessful()) {
//                    orderTransactionService.updateOrderStatus(orderId, REFUND_COMPLETED);
//                }
//            } catch(Exception e) {
//                throw new OrderException(ORDER_PAYMENT_FAILED, "orderId=" + orderId + ", message=" + e.getMessage());
//            }
//        }else{
//            String msg = String.format("환불은 주문의 상태가 배송 중이거나 배송 완료일 때 가능합니다.(orderId: %s, orderStatus: %s)",
//                    order.getId(), order.getStatus());
//            throw new OrderException(ORDER_REQUEST_NOT_ALLOWED, msg);
//        }
//
//        return OrderStatusInfo.from(order);
//    }

    private String generateNewOrderNum() {
        Long nextSequenceValue = orderRepository.getNextSequenceNum();

        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String sequencePart = String.format("%08d", nextSequenceValue);

        return datePart + "-" + sequencePart;
    }

}
