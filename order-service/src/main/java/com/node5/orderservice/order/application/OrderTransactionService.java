package com.node5.orderservice.order.application;

import com.node5.orderservice.order.client.CatalogClient;
import com.node5.orderservice.order.client.SettlementClient;
import com.node5.orderservice.order.domain.*;
import com.nohttps://github.com/prgrms-be-adv-devcourse/beadv2_2_Node5_BE/pull/424/conflict?name=order-service%252Fsrc%252Fmain%252Fjava%252Fcom%252Fnode5%252Forderservice%252Forder%252Fapplication%252FOrderTransactionService.java&ancestor_oid=6bd57ce176d52d93dc49af58dcd66a942e181257&base_oid=978e343cdab358a499b18b17ac0fe62fcc309406&head_oid=19a85a229047003d4b82bde2b938b48f8870728dde5.orderservice.order.exception.OrderException;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.node5.orderservice.order.exception.OrderErrorCode.ORDER_NOT_FOUND;

import static com.node5.orderservice.order.exception.OrderErrorCode.ORDER_GET_SHOPID_FAILED;
import static com.node5.orderservice.order.exception.OrderErrorCode.ORDER_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderTransactionService {

    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;
    private final SettlementClient settlementClient;
    private final OrderItemRepository orderItemRepository;

    // Order status 업데이트
    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus status){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND, "orderId=" + orderId));

        order.updateStatus(status);
    }

    // OrderItem status 업데이트
    @Transactional
    public void updateOrderItemStatus(OrderProgress fromStatus, OrderProgress toStatus) {
        orderItemRepository.updateStatusByCreatedAtBefore(fromStatus, toStatus);
    }

//    @Transactional
//    public void processSettlementRequest() {
//        // 1. CONFIRMED 주문 목록 조회
//        List<Order> confirmedOrders = orderRepository.findByStatus(OrderStatus.CONFIRMED);
//        if (confirmedOrders.isEmpty()) {
//            log.info("정산 요청을 보낼 CONFIRMED 상태의 주문 없음");
//            return;
//        }
//
//        log.info("구매 확정된 주문을 정산 테이블에 적재");
//        // 2. 모든 주문 ID 수집
//        List<UUID> confirmedOrderIds = confirmedOrders.stream()
//                .map(Order::getId)
//                .toList();
//
//        // 3. 해당 주문들의 모든 OrderItem을 한번에 조회
//        List<OrderItem> allOrderItems = orderItemRepository.findByOrderIdIn(confirmedOrderIds);
//
//        Map<UUID, List<OrderItem>> itemsByOrderId = allOrderItems.stream()
//                .collect(Collectors.groupingBy(OrderItem::getOrderId));
//
//        List<OrderWithItems> combinedData = confirmedOrders.stream()
//                .map(order -> new OrderWithItems(
//                        order, itemsByOrderId.getOrDefault(order.getId(), Collections.emptyList())
//                ))
//                .toList();
//
//        // 4. Product ID로 Shop ID 조회
//        List<UUID> allProductIds = allOrderItems.stream()
//                .map(OrderItem::getProductId)
//                .distinct()
//                .toList();
//
//        // Product ID, Shop ID
//        Map<UUID, UUID> productIdToShopIdMap;
//        ResponseEntity<Map<UUID, UUID>> responseEntity = catalogClient.getShopIdsByProductIds(allProductIds);
//
//        if (responseEntity.getStatusCode().is2xxSuccessful()) {
//            productIdToShopIdMap = Optional.ofNullable(responseEntity.getBody())
//                    .orElse(Collections.emptyMap());
//        } else {
//            throw new OrderGetShopIdFailed();
//        }
//
//        // 5. 정산 데이터 가공해서 SettlementSourceItem 생성
//        List<SettlementSourceItem> settlementItems = combinedData.stream()
//                .flatMap(data -> data.items().stream()
//                        .map(item -> new SettlementSourceItem(
//                                item.getProductId(),
//                                productIdToShopIdMap.get(item.getProductId()),
//                                item.getOrderId(),
//                                item.getTotalPrice(),
//                                data.order().getPaidAt()
//                        ))
//                )
//                .toList();
//
//        try {
//            // 6. 정산 서비스 API 호출
//            settlementClient.settle(settlementItems);
//
//            // 7. API 호출 성공 시에만 상태를 SETTLEMENT_REQUESTED로 업데이트
//            confirmedOrders.forEach(order -> order.updateStatus(OrderStatus.SETTLEMENT_REQUESTED));
//            orderRepository.saveAll(confirmedOrders);
//
//            log.info("{}건의 주문을 정산 요청 상태로 업데이트 완료", confirmedOrders.size());
//
//        } catch (FeignException e) {
//            log.error("정산 서비스 API 호출 실패. 다음 실행 시 재시도 예정", e);
//        }
//    }

}
