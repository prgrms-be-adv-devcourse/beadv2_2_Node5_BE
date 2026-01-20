package com.node5.orderservice.order.application;

import com.node5.orderservice.order.client.CatalogClient;
import com.node5.orderservice.order.client.SettlementClient;
import com.node5.orderservice.order.client.dto.SettlementSourceItem;
import com.node5.orderservice.order.domain.*;
import com.node5.orderservice.order.exception.OrderException;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    private final FeignErrorDecoderUtil feignUtil;

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

    @Transactional
    public void processSettlementRequest() {
        log.info("구매 확정된 상품 정보를 정산 테이블에 적재 시도");

        // CONFIRMED 상태의 주문 상품 목록 조회
        List<OrderItem> orderItems = orderItemRepository.findByStatus(OrderProgress.CONFIRMED);

        // Product ID로 Shop ID 조회 (catalog-client 연동)
        List<UUID> allProductIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .distinct()
                .toList();

        Map<UUID, UUID> productIdToShopIdMap;
        try {
            ResponseEntity<Map<UUID, UUID>> responseEntity = catalogClient.getShopIdsByProductIds(allProductIds);
            productIdToShopIdMap = Optional.ofNullable(responseEntity.getBody())
                    .orElse(Collections.emptyMap());
        } catch (FeignException e) {
            throw new OrderException(ORDER_GET_SHOPID_FAILED, "message=" + feignUtil.getFeignErrorMessage(e));
        } catch (Exception e) {
            throw new OrderException(ORDER_GET_SHOPID_FAILED, "message=" + e.getMessage());
        }

        // SettlementSourceItem 생성
        List<SettlementSourceItem> settlementItems = orderItems.stream()
                .map(item -> new SettlementSourceItem(
                        item.getProductId(),
                        productIdToShopIdMap.get(item.getProductId()),
                        item.getOrderId(),
                        item.getTotalPrice(),
                        item.getCreatedAt()
                ))
                .toList();

        try {
            // 정산 서비스 API 호출
            settlementClient.settle(settlementItems);

            // API 호출 성공 시 OrderItem의 settlementStatus를 REGISTERED로 업데이트
            List<UUID> orderItemIds = orderItems.stream()
                    .map(OrderItem::getId)
                    .collect(Collectors.toList());
            orderItemRepository.updateSettlementStatus(orderItemIds, OrderItemSettlementStatus.REGISTERED);

            log.info("{}건의 주문을 정산 요청 상태로 업데이트 완료", orderItems.size());
        } catch (FeignException e) {
            log.error("정산 서비스 API 호출 실패. 다음 실행 시 재시도 예정", e);
        }
    }

}
