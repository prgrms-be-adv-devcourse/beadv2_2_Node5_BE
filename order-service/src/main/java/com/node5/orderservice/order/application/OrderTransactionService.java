package com.node5.orderservice.order.application;

import com.node5.orderservice.order.application.dto.OrderWithItems;
import com.node5.orderservice.order.client.CatalogClient;
import com.node5.orderservice.order.client.SettlementClient;
import com.node5.orderservice.order.client.dto.SettlementSourceItem;
import com.node5.orderservice.order.domain.*;
import com.node5.orderservice.order.exception.OrderGetShopIdFailed;
import com.node5.orderservice.order.exception.OrderNotFoundException;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderTransactionService {

    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;
    private final SettlementClient settlementClient;
    private final OrderItemRepository orderItemRepository;


    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus status){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.updateStatus(status);
    }

    // OrderStatus가 PAID인 주문의 상태를 DELIVERY_ING로 일괄 업데이트
    @Transactional
    public void updateToDeliveryIng(LocalDateTime standard) {
        List<Order> paidOrders = orderRepository.findByStatusAndPaidAtBefore(OrderStatus.PAID, standard);

        if(!paidOrders.isEmpty()){
            paidOrders.forEach(order -> order.updateStatus(OrderStatus.DELIVERY_ING));
            orderRepository.saveAll(paidOrders);
        }
    }

    // OrderStatus가 DELIVERY_ING인 주문의 상태를 DELIVERY_COMPLETED로 일괄 업데이트
    @Transactional
    public void updateToDeliveryCompleted(LocalDateTime standard) {
        List<Order> deliveryIngOrders = orderRepository.findByStatusAndModifiedAtBefore(OrderStatus.DELIVERY_ING, standard);

        if(!deliveryIngOrders.isEmpty()){
            deliveryIngOrders.forEach(order -> order.updateStatus(OrderStatus.DELIVERY_COMPLETED));
            orderRepository.saveAll(deliveryIngOrders);
        }
    }

    // OrderStatus가 DELIVERY_COMPLETED인 주문의 상태를 CONFIRMED로 일괄 업데이트
    public void updateToConfirmed(LocalDateTime standard) {
        List<Order> deliveryCompletedOrders = orderRepository.findByStatusAndModifiedAtBefore(OrderStatus.DELIVERY_COMPLETED, standard);

        if(!deliveryCompletedOrders.isEmpty()){
            deliveryCompletedOrders.forEach(order -> order.updateStatus(OrderStatus.CONFIRMED));
            orderRepository.saveAll(deliveryCompletedOrders);
        }
    }

    @Transactional
    public void processSettlementRequest() {
        // 1. CONFIRMED 주문 목록 조회
        List<Order> confirmedOrders = orderRepository.findByStatus(OrderStatus.CONFIRMED);
        if (confirmedOrders.isEmpty()) {
            log.info("정산 요청을 보낼 CONFIRMED 상태의 주문이 없습니다.");
            return;
        }

        // 2. 모든 주문 ID 수집
        List<UUID> confirmedOrderIds = confirmedOrders.stream()
                .map(Order::getId)
                .toList();

        // 3. 해당 주문들의 모든 OrderItem을 한번에 조회
        List<OrderItem> allOrderItems = orderItemRepository.findByOrderIdIn(confirmedOrderIds);

        Map<UUID, List<OrderItem>> itemsByOrderId = allOrderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        List<OrderWithItems> combinedData = confirmedOrders.stream()
                .map(order -> new OrderWithItems(
                        order, itemsByOrderId.getOrDefault(order.getId(), Collections.emptyList())
                ))
                .toList();

        // 4. Product ID로 Shop ID 조회
        List<UUID> allProductIds = allOrderItems.stream()
                .map(OrderItem::getProductId)
                .distinct()
                .toList();

        // Product ID, Shop ID
        Map<UUID, UUID> productIdToShopIdMap;
        ResponseEntity<Map<UUID, UUID>> responseEntity = catalogClient.getShopIdsByProductIds(allProductIds);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            productIdToShopIdMap = Optional.ofNullable(responseEntity.getBody())
                    .orElse(Collections.emptyMap());
        } else {
            throw new OrderGetShopIdFailed();
        }

        // 5. 정산 데이터 가공해서 SettlementSourceItem 생성
        List<SettlementSourceItem> settlementItems = combinedData.stream()
                .flatMap(data -> data.items().stream()
                        .map(item -> new SettlementSourceItem(
                                item.getProductId(),
                                productIdToShopIdMap.get(item.getProductId()),
                                item.getOrderId(),
                                item.getTotalPrice(),
                                data.order().getPaidAt()
                        ))
                )
                .toList();

        try {
            // 6. 정산 서비스 API 호출
            settlementClient.settle(settlementItems);

            // 7. API 호출 성공 시에만 상태를 SETTLEMENT_REQUESTED로 업데이트
            confirmedOrders.forEach(order -> order.updateStatus(OrderStatus.SETTLEMENT_REQUESTED));
            orderRepository.saveAll(confirmedOrders);

            log.info("{}건의 주문을 정산 요청 상태로 업데이트 완료", confirmedOrders.size());

        } catch (FeignException e) {
            log.error("정산 서비스 API 호출에 실패했습니다. 다음 실행 시 재시도됩니다.", e);
        }
    }
}
