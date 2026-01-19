package com.node5.supportservice.notification.domain.status;

import com.node5.common.event.SubscriptionStatusChangedEvent;
import com.node5.supportservice.notification.domain.NotificationChannel;
import com.node5.supportservice.notification.domain.message.NotificationMessage;
import com.node5.supportservice.notification.domain.message.SubscriptionStatusNotificationMessage;

import java.util.Optional;
import java.util.Set;

public enum SubscriptionStatus {
    ACTIVE {
        @Override
        public Set<NotificationChannel> channels() {
            return Set.of(NotificationChannel.EMAIL);
        }

        @Override
        public NotificationMessage message(SubscriptionStatusChangedEvent event) {
            return new SubscriptionStatusNotificationMessage(
                    event.memberId(),
                    "구독 시작",
                    "상품 구독이 시작되었습니다."
            );
        }
    },
    PAUSED {
        @Override
        public Set<NotificationChannel> channels() {
            return Set.of(NotificationChannel.EMAIL);
        }

        @Override
        public NotificationMessage message(SubscriptionStatusChangedEvent event) {
            return new SubscriptionStatusNotificationMessage(
                    event.memberId(),
                    "구독 일시정지",
                    "상품 구독이 일시정지 되었습니다."
            );
        }
    },
    FAILED {
        @Override
        public Set<NotificationChannel> channels() {
            return Set.of(NotificationChannel.EMAIL);
        }

        @Override
        public NotificationMessage message(SubscriptionStatusChangedEvent event) {
            return new SubscriptionStatusNotificationMessage(
                    event.memberId(),
                    "구독 실패",
                    "상품 구독에 실패했습니다."
            );
        }
    },
    CANCELLED {
        @Override
        public Set<NotificationChannel> channels() {
            return Set.of(NotificationChannel.EMAIL);
        }

        @Override
        public NotificationMessage message(SubscriptionStatusChangedEvent event) {
            return new SubscriptionStatusNotificationMessage(
                    event.memberId(),
                    "구독 취소",
                    "상품 구독을 취소했습니다."
            );
        }
    },
    UNAVAILABLE {
        @Override
        public Set<NotificationChannel> channels() {
            return Set.of(NotificationChannel.EMAIL);
        }

        @Override
        public NotificationMessage message(SubscriptionStatusChangedEvent event) {
            return new SubscriptionStatusNotificationMessage(
                    event.memberId(),
                    "구독 불가능",
                    "상품 구독이 불가능하게 되었습니다."
            );
        }
    };

    public abstract Set<NotificationChannel> channels();

    public abstract NotificationMessage message(
            SubscriptionStatusChangedEvent event
    );

    public static Optional<SubscriptionStatus> from(String value) {
        try {
            return Optional.of(SubscriptionStatus.valueOf(value.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
