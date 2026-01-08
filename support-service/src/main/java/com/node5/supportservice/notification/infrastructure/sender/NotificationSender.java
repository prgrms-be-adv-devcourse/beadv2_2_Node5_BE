package com.node5.supportservice.notification.infrastructure.sender;

import com.node5.supportservice.notification.domain.message.NotificationMessage;

public interface NotificationSender {
    void send(NotificationMessage message);
}
