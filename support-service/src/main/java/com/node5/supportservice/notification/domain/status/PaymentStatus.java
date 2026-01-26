package com.node5.supportservice.notification.domain.status;

import com.node5.common.event.PaymentSendEmailEvent;
import com.node5.supportservice.notification.domain.NotificationChannel;
import com.node5.supportservice.notification.domain.message.NotificationMessage;
import com.node5.supportservice.notification.domain.message.PaymentStatusNotificationMessage;

import java.util.Optional;
import java.util.Set;

public enum PaymentStatus {
    CONFIRMED {
        @Override
        public Set<NotificationChannel> channels() {
            return Set.of(NotificationChannel.EMAIL);
        }

        @Override
        public NotificationMessage message(PaymentSendEmailEvent event) {
            String body = "회원님의 결제 금액 " + event.amount() + "원이 승인되었습니다.";
            return new PaymentStatusNotificationMessage(
                    event.memberId().toString(),
                    "결재 승인",
                    body
            );
        }
    },
    PAYMENT_FAILED {
        @Override
        public Set<NotificationChannel> channels() {
            return Set.of(NotificationChannel.EMAIL);
        }

        @Override
        public NotificationMessage message(PaymentSendEmailEvent event) {
            String body = "회원님의 결제 금액 " + event.amount() + "원이 승인 실패되었습니다.\n" +
                    "실패 사유: " + event.failureReason();
            return new PaymentStatusNotificationMessage(
                    event.memberId().toString(),
                    "결재 승인 실패",
                    body
            );
        }
    },
    CANCELED {
        @Override
        public Set<NotificationChannel> channels() {
            return Set.of(NotificationChannel.EMAIL);
        }

        @Override
        public NotificationMessage message(PaymentSendEmailEvent event) {
            String body = "회원님의 결제 금액 " + event.amount() + "원이 환불되었습니다.";
            return new PaymentStatusNotificationMessage(
                    event.memberId().toString(),
                    "결재 환불",
                    body
            );
        }
    },
    CANCEL_FAILED {
        @Override
        public Set<NotificationChannel> channels() {
            return Set.of(NotificationChannel.EMAIL);
        }

        @Override
        public NotificationMessage message(PaymentSendEmailEvent event) {
            String body = "회원님의 결제 금액 " + event.amount() + "원이 환불 실패했습니다.\n" +
                    "실패 사유: " + event.failureReason();
            return new PaymentStatusNotificationMessage(
                    event.memberId().toString(),
                    "결재 환불 실패",
                    body
            );
        }
    },
    MANUAL_PROCESSING_REQUIRED {
        @Override
        public Set<NotificationChannel> channels() {
            return Set.of(NotificationChannel.EMAIL);
        }

        @Override
        public NotificationMessage message(PaymentSendEmailEvent event) {
            String body = "회원님의 결제 금액 " + event.amount() + "원이 환불 실패했습니다.\n" +
                    "관리자에세 문의바랍니다.\n" +
                    "실패 사유: " + event.failureReason();
            return new PaymentStatusNotificationMessage(
                    event.memberId().toString(),
                    "결재 환불 실패",
                    body
            );
        }
    };

    public abstract Set<NotificationChannel> channels();

    public abstract NotificationMessage message(
            PaymentSendEmailEvent event
    );

    public static Optional<PaymentStatus> from(String value) {
        try {
            return Optional.of(PaymentStatus.valueOf(value.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
