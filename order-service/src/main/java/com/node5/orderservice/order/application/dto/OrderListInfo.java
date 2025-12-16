package com.node5.orderservice.order.application.dto;

import com.node5.common.domain.PageInfoDto;
import com.node5.orderservice.order.domain.Order;
import com.node5.orderservice.order.domain.OrderStatus;
import com.node5.orderservice.order.domain.OrderType;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public record OrderListInfo(
        PageInfoDto pageInfo,
        List<OrderListDetailInfo> orderList
) {

    public record OrderListDetailInfo (
            // 주문 정보
            UUID orderId,
            String orderNum,
            String orderDate,
            OrderStatus status,
            OrderType orderType,
            UUID subscriptionId,
            BigDecimal totalAmount,

            // 주문 내 상품 정보
            List<OrderItemInfo> orderedItems
    ) {

        public static OrderListDetailInfo from(Order order, List<OrderItemInfo> orderedItems) {
            // 주문 날짜 포맷팅
            DateTimeFormatter orderDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String orderDate = order.getCreatedAt().format(orderDateFormatter);

            return new OrderListDetailInfo(
                    order.getId(),
                    order.getOrderNum(),
                    orderDate,
                    order.getStatus(),
                    order.getOrderType(),
                    order.getSubscriptionId(),
                    order.getTotalAmount(),
                    orderedItems
            );
        }
    }

    public static OrderListInfo from(PageInfoDto pageInfo, List<OrderListDetailInfo> orderList) {
        return new OrderListInfo(pageInfo, orderList);
    }

}
