package com.node5.supportservice.notification.domain.message;

public record OrderStatusNotificationMessage(
        String memberId,
        String title,
        String body
) implements NotificationMessage {

}
