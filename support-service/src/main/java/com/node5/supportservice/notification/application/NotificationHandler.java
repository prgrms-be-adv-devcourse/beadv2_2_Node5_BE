package com.node5.supportservice.notification.application;

import com.node5.common.event.OrderStatusChangedEvent;
import com.node5.common.event.PaymentSendEmailEvent;
import com.node5.common.event.SubscriptionStatusChangedEvent;
import com.node5.supportservice.notification.domain.NotificationChannel;
import com.node5.supportservice.notification.domain.message.NotificationMessage;
import com.node5.supportservice.notification.domain.status.OrderStatus;
import com.node5.supportservice.notification.domain.status.PaymentStatus;
import com.node5.supportservice.notification.domain.status.SubscriptionStatus;
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

    public void subscriptionStatusChangedHandle(SubscriptionStatusChangedEvent event) {
        SubscriptionStatus.from(event.subscriptionStatus())
                .ifPresent(status -> {
                    NotificationMessage message = status.message(event);
                    Set<NotificationChannel> channels = status.channels();

                    if (message == null || channels.isEmpty()) return;

                    channels.forEach(channel ->
                            notificationMessageProducer.produce(channel, message)
                    );
                });
    }

    public void paymentSendEmailConsume(PaymentSendEmailEvent event) {
        PaymentStatus.from(event.status())
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
