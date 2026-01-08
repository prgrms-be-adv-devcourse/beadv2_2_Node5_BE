package com.node5.supportservice.notification.application;

import com.node5.common.event.OrderStatusChangedEvent;
import com.node5.common.event.SubscribeStatusChangedEvent;
import com.node5.supportservice.notification.domain.NotificationChannel;
import com.node5.supportservice.notification.domain.message.NotificationMessage;
import com.node5.supportservice.notification.domain.status.OrderStatus;
import com.node5.supportservice.notification.domain.status.SubscribeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class NotificationHandler {

    private final NotificationMessageProducer notificationMessageProducer;

    public void orderStatusChangedHandle(OrderStatusChangedEvent event) {
        OrderStatus.from(event.orderStatus())
                .ifPresent(status -> {
                    NotificationMessage message = status.message(event);
                    Set<NotificationChannel> channels = status.channels();

                    if (message == null || channels.isEmpty()) return;

                    channels.forEach(channel ->
                            notificationMessageProducer.produce(channel, message)
                    );
                });
    }

    public void subscribeStatusChangedHandle(SubscribeStatusChangedEvent event) {
        SubscribeStatus.from(event.subscribeStatus())
                .ifPresent(status -> {
                    NotificationMessage message = status.message(event);
                    Set<NotificationChannel> channels = status.channels();

                    if (message == null || channels.isEmpty()) return;

                    channels.forEach(channel ->
                            notificationMessageProducer.produce(channel, message)
                    );
                });
    }
}
