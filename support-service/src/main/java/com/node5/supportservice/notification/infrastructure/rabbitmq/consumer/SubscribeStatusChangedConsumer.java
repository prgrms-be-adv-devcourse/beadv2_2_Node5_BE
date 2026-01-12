package com.node5.supportservice.notification.infrastructure.rabbitmq.consumer;

import com.node5.common.event.SubscribeStatusChangedEvent;
import com.node5.supportservice.notification.application.NotificationHandler;
import com.node5.supportservice.notification.infrastructure.rabbitmq.config.RabbitmqConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscribeStatusChangedConsumer {

    private final NotificationHandler notificationHandler;

    @RabbitListener(queues = RabbitmqConfig.SUBSCRIBE_STATUS_QUEUE)
    public void consume(SubscribeStatusChangedEvent event) {
        notificationHandler.subscribeStatusChangedHandle(event);
    }
}
