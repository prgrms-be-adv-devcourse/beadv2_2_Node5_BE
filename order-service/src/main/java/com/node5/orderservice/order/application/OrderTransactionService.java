package com.node5.orderservice.order.application;

import com.node5.orderservice.order.domain.Order;
import com.node5.orderservice.order.domain.OrderRepository;
import com.node5.orderservice.order.domain.OrderStatus;
import com.node5.orderservice.order.exception.OrderNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderTransactionService {

    @Autowired
    private OrderRepository orderRepository;

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

        // TODO 정산 서비스 API 호출
    }
}
