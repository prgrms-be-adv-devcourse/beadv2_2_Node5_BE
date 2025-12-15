package com.node5.orderservice.order.application.dto;

import com.node5.orderservice.order.domain.Order;
import com.node5.orderservice.order.domain.OrderStatus;
import com.node5.orderservice.order.domain.OrderType;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public record OrderDetailInfo(
        UUID orderId,
        String orderNum,
        String orderDate,
        OrderStatus status,
        OrderType orderType,
        BigDecimal totalAmount, // 주문 총액

        List<OrderItemInfo> orderedItems,
        DeliveryInfo deliveryInfo,
        PaymentInfo paymentInfo
) {

    // 배송지 정보
    public record DeliveryInfo(
            String recipientName,
            String recipientAddress
    ) {
    }

    // 결제 정보
    public record PaymentInfo(
            BigDecimal paidAmount, // 결제 금액
            String transactionDate // 결제 일시
    ) {
    }

    public static OrderDetailInfo from(Order order, List<OrderItemInfo> orderedItems) {
        DateTimeFormatter orderDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String orderDate = order.getCreatedAt().format(orderDateFormatter);

        DeliveryInfo deliveryInfo = new DeliveryInfo(
                order.getRecipientName(),
                order.getRecipientAddress()
        );

        DateTimeFormatter paidAtFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        PaymentInfo paymentInfo = new PaymentInfo(
                order.getTotalAmount(),
                order.getPaidAt() != null ? order.getPaidAt().format(paidAtFormatter) : null
        );

        return new OrderDetailInfo(
                order.getId(),
                order.getOrderNum(),
                orderDate,
                order.getStatus(),
                order.getOrderType(),
                order.getTotalAmount(),
                orderedItems,
                deliveryInfo,
                paymentInfo
        );
    }
}

