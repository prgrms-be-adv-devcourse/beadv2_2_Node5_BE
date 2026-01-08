package com.node5.supportservice.notification.worker;

import com.node5.supportservice.notification.domain.message.NotificationMessage;

public interface NotificationWorker {
    void consume(NotificationMessage message);
}
