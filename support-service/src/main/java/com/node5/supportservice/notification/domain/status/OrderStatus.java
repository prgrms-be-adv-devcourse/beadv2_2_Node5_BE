package com.node5.supportservice.notification.domain.status;

import com.node5.common.event.OrderStatusChangedEvent;
import com.node5.supportservice.notification.domain.NotificationChannel;
import com.node5.supportservice.notification.domain.message.NotificationMessage;
import com.node5.supportservice.notification.domain.message.OrderStatusNotificationMessage;

import java.util.Optional;
import java.util.Set;

public enum OrderStatus {
    PAID {
        @Override
        public Set<NotificationChannel> channels() {
            return Set.of(NotificationChannel.EMAIL);
        }

        @Override
        public NotificationMessage message(OrderStatusChangedEvent event) {
            return new OrderStatusNotificationMessage(
                    event.memberId(),
                    "주문 완료",
                    "주문하신 상품 결제가 완료되었습니다."
            );
        }
    },
    DELIVERY_ING {
        @Override
        public Set<NotificationChannel> channels() {
            return Set.of(NotificationChannel.EMAIL);
        }

        @Override
        public NotificationMessage message(OrderStatusChangedEvent event) {
            return new OrderStatusNotificationMessage(
                    event.memberId(),
                    "배송 시작",
                    "주문하신 상품의 배송이 시작되었습니다."
            );
        }
    },
    REFUND_COMPLETED {
        @Override
        public Set<NotificationChannel> channels() {
            return Set.of(NotificationChannel.EMAIL);
        }

        @Override
        public NotificationMessage message(OrderStatusChangedEvent event) {
            return new OrderStatusNotificationMessage(
                    event.memberId(),
                    "주문 환불 완료",
                    "주문하신 상품의 환불이 완료되었습니다."
            );
        }
    };

    public abstract Set<NotificationChannel> channels();

    public abstract NotificationMessage message(
            OrderStatusChangedEvent event
    );

    public static Optional<OrderStatus> from(String value) {
        try {
            return Optional.of(OrderStatus.valueOf(value.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
