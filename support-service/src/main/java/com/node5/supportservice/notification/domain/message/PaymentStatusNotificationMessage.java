package com.node5.supportservice.notification.domain.message;

public record PaymentStatusNotificationMessage(
        String memberId,
        String title,
        String body
) implements NotificationMessage {
}
