package com.node5.supportservice.notification.application;

import com.node5.supportservice.notification.domain.NotificationChannel;
import com.node5.supportservice.notification.domain.message.NotificationMessage;

public interface NotificationMessageProducer {
    void produce(NotificationChannel channel, NotificationMessage message);
}
